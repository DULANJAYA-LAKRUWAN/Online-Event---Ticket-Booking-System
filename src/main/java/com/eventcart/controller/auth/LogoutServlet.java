package com.eventcart.controller.auth;

import com.eventcart.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller managing User Logout.
 * Invalidates the HTTP session and clears security context.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doLogout(req, resp);
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            String userEmail = (String) session.getAttribute("userName");
            logger.info("Invalidating session for user: {}", userEmail);
            session.invalidate();
        }

        // Redirect to login with logout query parameter
        resp.sendRedirect(req.getContextPath() + "/login?logout=true");
    }
}
