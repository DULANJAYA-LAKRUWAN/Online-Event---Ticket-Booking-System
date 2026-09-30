package com.eventcart.controller.checkout;

import com.eventcart.controller.BaseServlet;
import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartValidationResult;
import com.eventcart.dto.CheckoutDto;
import com.eventcart.entity.Booking;
import com.eventcart.entity.User;
import com.eventcart.exception.InventoryConflictException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.BookingService;
import com.eventcart.service.CartService;
import com.eventcart.service.impl.BookingServiceImpl;
import com.eventcart.service.impl.CartServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller managing checkout presentation, authentication enforcement,
 * transactional inventory reservation, and booking confirmation.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private CartService cartService;
    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.cartService = new CartServiceImpl();
        this.bookingService = new BookingServiceImpl();
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User authUser = getAuthenticatedUser(req);
        if (authUser == null) {
            setFlash(req, "warning", "Please sign in to proceed with ticket checkout.");
            String returnUrl = URLEncoder.encode(req.getContextPath() + "/checkout", StandardCharsets.UTF_8);
            redirect(req, resp, "/login?returnUrl=" + returnUrl);
            return;
        }

        // Check if viewing a confirmed booking receipt
        String confirmedRef = req.getParameter("confirmed");
        if (confirmedRef != null && !confirmedRef.trim().isEmpty()) {
            Optional<Booking> optBooking = bookingService.getBookingByReference(confirmedRef.trim());
            if (optBooking.isPresent()) {
                Booking booking = optBooking.get();
                // Ensure customer only views their own booking or admin views
                if (booking.getUser().getId().equals(authUser.getId()) || "ADMIN".equals(authUser.getRole().name())) {
                    req.setAttribute("booking", booking);
                    req.setAttribute("pageActive", "checkout");
                    forward(req, resp, "checkout/confirmation");
                    return;
                }
            }
        }

        HttpSession session = req.getSession(true);
        CartDto cart = cartService.getCart(session);

        if (cart == null || cart.isEmpty()) {
            setFlash(req, "info", "Your shopping cart is currently empty. Please select tickets before checking out.");
            redirect(req, resp, "/cart");
            return;
        }

        // Fresh server-side validation of stock and event state
        CartValidationResult validationResult = cartService.validateCart(cart);
        if (!validationResult.isValid()) {
            setFlash(req, "danger", "Cart inventory updated: " + String.join(", ", validationResult.getErrorMessages()));
            redirect(req, resp, "/cart");
            return;
        }

        CheckoutDto checkout = new CheckoutDto(
                authUser.getId(),
                authUser.getFullName(),
                authUser.getEmail(),
                authUser.getPhoneNumber(),
                cart
        );

        req.setAttribute("checkout", checkout);
        req.setAttribute("cartValidation", validationResult);
        req.setAttribute("pageActive", "checkout");

        forward(req, resp, "checkout/checkout");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User authUser = getAuthenticatedUser(req);
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        if (authUser == null) {
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required to complete checkout.");
            } else {
                setFlash(req, "warning", "Please sign in to proceed with checkout.");
                redirect(req, resp, "/login?returnUrl=" + URLEncoder.encode(req.getContextPath() + "/checkout", StandardCharsets.UTF_8));
            }
            return;
        }

        HttpSession session = req.getSession(true);
        CartDto cart = cartService.getCart(session);

        if (cart == null || cart.isEmpty()) {
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Shopping cart is empty.");
            } else {
                setFlash(req, "warning", "Your cart is empty.");
                redirect(req, resp, "/cart");
            }
            return;
        }

        try {
            // Reserve inventory and create booking draft within row-locking database transaction
            Booking booking = bookingService.createBookingDraft(authUser, cart);

            // Successfully reserved: clear session cart
            cartService.clearCart(session);

            if (isAjax) {
                Map<String, Object> data = new HashMap<>();
                data.put("bookingReference", booking.getBookingReference());
                data.put("redirectUrl", req.getContextPath() + "/checkout?confirmed=" + booking.getBookingReference());
                sendJsonOk(resp, "Ticket reservation confirmed!", data);
            } else {
                setFlash(req, "success", "Your tickets have been reserved successfully!");
                redirect(req, resp, "/checkout?confirmed=" + booking.getBookingReference());
            }

        } catch (InventoryConflictException e) {
            logger.warn("Inventory conflict during checkout for user {}: {}", authUser.getEmail(), e.getMessage());
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
            } else {
                setFlash(req, "danger", e.getMessage());
                redirect(req, resp, "/cart");
            }
        } catch (ValidationException e) {
            logger.warn("Validation error during checkout: {}", e.getMessage());
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } else {
                setFlash(req, "danger", e.getMessage());
                redirect(req, resp, "/cart");
            }
        } catch (Exception e) {
            logger.error("Checkout transaction error: {}", e.getMessage(), e);
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while reserving your tickets.");
            } else {
                setFlash(req, "danger", "Unable to complete reservation at this time. Please try again.");
                redirect(req, resp, "/checkout");
            }
        }
    }
}
