package com.eventcart.controller.cart;

import com.eventcart.controller.BaseServlet;
import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartItemDto;
import com.eventcart.exception.InventoryConflictException;
import com.eventcart.exception.ResourceNotFoundException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.CartService;
import com.eventcart.service.impl.CartServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * REST-like AJAX Controller for all asynchronous shopping cart operations.
 */
@WebServlet(name = "CartServlet", urlPatterns = {
        "/cart/add",
        "/cart/update",
        "/cart/remove",
        "/cart/clear",
        "/cart/count",
        "/cart/data"
})
public class CartServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private CartService cartService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.cartService = new CartServiceImpl();
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(true);

        try {
            if ("/cart/count".equals(path)) {
                int count = cartService.getCartCount(session);
                Map<String, Object> data = new HashMap<>();
                data.put("cartCount", count);
                sendJsonOk(resp, "Cart count retrieved", data);
            } else if ("/cart/data".equals(path)) {
                CartDto cart = cartService.getCart(session);
                sendJsonOk(resp, "Cart data retrieved", cart);
            } else {
                sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Resource not found.");
            }
        } catch (Exception e) {
            logger.error("Error processing cart GET request: {}", e.getMessage(), e);
            sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to retrieve cart data.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(true);

        try {
            switch (path) {
                case "/cart/add":
                    handleAdd(req, resp, session);
                    break;
                case "/cart/update":
                    handleUpdate(req, resp, session);
                    break;
                case "/cart/remove":
                    handleRemove(req, resp, session);
                    break;
                case "/cart/clear":
                    handleClear(req, resp, session);
                    break;
                default:
                    sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
                    break;
            }
        } catch (InventoryConflictException e) {
            logger.warn("Inventory conflict in cart operation: {}", e.getMessage());
            sendJsonError(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (ValidationException | ResourceNotFoundException e) {
            logger.warn("Validation error in cart operation: {}", e.getMessage());
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in cart servlet: {}", e.getMessage(), e);
            sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "A server error occurred while updating your cart.");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String ticketIdStr = req.getParameter("ticketTypeId");
        String qtyStr = req.getParameter("quantity");

        if (ticketIdStr == null || ticketIdStr.trim().isEmpty()) {
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ticket type is required.");
            return;
        }

        Long ticketTypeId;
        int quantity = 1;

        try {
            ticketTypeId = Long.parseLong(ticketIdStr.trim());
            if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                quantity = Integer.parseInt(qtyStr.trim());
            }
        } catch (NumberFormatException e) {
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ticket ID or quantity format.");
            return;
        }

        CartItemDto addedItem = cartService.addItem(session, ticketTypeId, quantity);
        CartDto cart = cartService.getCart(session);

        Map<String, Object> data = new HashMap<>();
        data.put("cartCount", cart.getTotalQuantity());
        data.put("totalAmount", cart.getTotalAmount());
        data.put("item", addedItem);

        sendJsonOk(resp, "Added " + quantity + "x " + addedItem.getTicketTypeName() + " to cart.", data);
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String ticketIdStr = req.getParameter("ticketTypeId");
        String qtyStr = req.getParameter("quantity");

        if (ticketIdStr == null || qtyStr == null) {
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ticket type and quantity are required.");
            return;
        }

        Long ticketTypeId;
        int quantity;

        try {
            ticketTypeId = Long.parseLong(ticketIdStr.trim());
            quantity = Integer.parseInt(qtyStr.trim());
        } catch (NumberFormatException e) {
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid number format.");
            return;
        }

        CartItemDto updated = cartService.updateItemQuantity(session, ticketTypeId, quantity);
        CartDto cart = cartService.getCart(session);

        Map<String, Object> data = new HashMap<>();
        data.put("cartCount", cart.getTotalQuantity());
        data.put("totalAmount", cart.getTotalAmount());
        data.put("isEmpty", cart.isEmpty());
        data.put("quantity", updated != null ? updated.getQuantity() : 0);
        data.put("itemSubtotal", updated != null ? updated.getSubtotal() : BigDecimal.ZERO);

        sendJsonOk(resp, "Cart updated successfully.", data);
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String ticketIdStr = req.getParameter("ticketTypeId");

        if (ticketIdStr == null || ticketIdStr.trim().isEmpty()) {
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Ticket type ID is required.");
            return;
        }

        Long ticketTypeId;
        try {
            ticketTypeId = Long.parseLong(ticketIdStr.trim());
        } catch (NumberFormatException e) {
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ticket ID.");
            return;
        }

        boolean removed = cartService.removeItem(session, ticketTypeId);
        CartDto cart = cartService.getCart(session);

        Map<String, Object> data = new HashMap<>();
        data.put("removed", removed);
        data.put("cartCount", cart.getTotalQuantity());
        data.put("totalAmount", cart.getTotalAmount());
        data.put("isEmpty", cart.isEmpty());

        sendJsonOk(resp, "Ticket removed from cart.", data);
    }

    private void handleClear(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        cartService.clearCart(session);

        Map<String, Object> data = new HashMap<>();
        data.put("cartCount", 0);
        data.put("totalAmount", BigDecimal.ZERO);
        data.put("isEmpty", true);

        sendJsonOk(resp, "Cart cleared successfully.", data);
    }
}
