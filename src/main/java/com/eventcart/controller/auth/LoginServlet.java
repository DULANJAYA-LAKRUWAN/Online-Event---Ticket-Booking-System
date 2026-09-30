package com.eventcart.controller.auth;

import com.eventcart.controller.BaseServlet;
import com.eventcart.dto.ApiResponse;
import com.eventcart.entity.User;
import com.eventcart.service.UserService;
import com.eventcart.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller managing User Authentication (Login).
 * Supports standard form submissions and asynchronous AJAX logins.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final UserService userService;

    public LoginServlet() {
        this(new UserServiceImpl());
    }

    public LoginServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User loggedUser = getAuthenticatedUser(req);
        if (loggedUser != null) {
            redirect(req, resp, "/home");
            return;
        }

        String returnUrl = req.getParameter("returnUrl");
        if (returnUrl != null && !returnUrl.trim().isEmpty()) {
            req.setAttribute("returnUrl", returnUrl);
        }

        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = getStringParameter(req, "email", "");
        String password = getStringParameter(req, "password", "");
        String returnUrl = getStringParameter(req, "returnUrl", "");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        Optional<User> userOpt = userService.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Prevent session fixation attack: invalidate existing session & create fresh one
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setAttribute("authUser", user);
            newSession.setAttribute("userId", user.getId());
            newSession.setAttribute("userRole", user.getRole().name());
            newSession.setAttribute("userName", user.getFullName());

            logger.info("Session established for user: {} (Role: {})", user.getEmail(), user.getRole());

            String target = (returnUrl != null && returnUrl.startsWith(req.getContextPath()))
                    ? returnUrl
                    : req.getContextPath() + "/home";

            if (isAjax) {
                sendJsonOk(resp, "Login successful", target);
            } else {
                resp.sendRedirect(target);
            }
        } else {
            String errorMsg = "Invalid email or password. Please check your credentials.";
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, errorMsg);
            } else {
                req.setAttribute("errorMessage", errorMsg);
                req.setAttribute("email", email);
                req.setAttribute("returnUrl", returnUrl);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        }
    }
}
