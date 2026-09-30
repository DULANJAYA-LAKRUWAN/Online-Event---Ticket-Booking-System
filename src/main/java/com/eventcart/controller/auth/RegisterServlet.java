package com.eventcart.controller.auth;

import com.eventcart.controller.BaseServlet;
import com.eventcart.entity.Role;
import com.eventcart.entity.User;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.UserService;
import com.eventcart.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller managing User Account Registration.
 * Supports attendee/customer and event organizer signups with server-side validation.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public RegisterServlet() {
        this(new UserServiceImpl());
    }

    public RegisterServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = getAuthenticatedUser(req);
        if (loggedUser != null) {
            redirect(req, resp, "/home");
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = getStringParameter(req, "fullName", "");
        String email = getStringParameter(req, "email", "");
        String phone = getStringParameter(req, "phoneNumber", "");
        String password = getStringParameter(req, "password", "");
        String confirmPassword = getStringParameter(req, "confirmPassword", "");
        String roleStr = getStringParameter(req, "role", "CUSTOMER");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        if (!password.equals(confirmPassword)) {
            handleError(req, resp, isAjax, "Passwords do not match.", fullName, email, phone, roleStr);
            return;
        }

        Role role = Role.CUSTOMER;
        if ("ORGANIZER".equalsIgnoreCase(roleStr)) {
            role = Role.ORGANIZER;
        }

        try {
            User newUser = userService.registerUser(email, password, fullName, phone, role);
            logger.info("New account created: id={}, email={}", newUser.getId(), newUser.getEmail());

            if (isAjax) {
                sendJsonOk(resp, "Account registered successfully! Please log in.", req.getContextPath() + "/login?registered=true");
            } else {
                setFlash(req, "success", "Registration successful! You can now log in.");
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (ValidationException ve) {
            logger.warn("Validation error during registration: {}", ve.getMessage());
            handleError(req, resp, isAjax, ve.getMessage(), fullName, email, phone, roleStr);
        } catch (Exception ex) {
            logger.error("Unexpected error during registration: {}", ex.getMessage(), ex);
            handleError(req, resp, isAjax, "An unexpected error occurred. Please try again.", fullName, email, phone, roleStr);
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, boolean isAjax,
                             String message, String fullName, String email, String phone, String role)
            throws ServletException, IOException {
        if (isAjax) {
            sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, message);
        } else {
            req.setAttribute("errorMessage", message);
            req.setAttribute("fullName", fullName);
            req.setAttribute("email", email);
            req.setAttribute("phoneNumber", phone);
            req.setAttribute("role", role);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
