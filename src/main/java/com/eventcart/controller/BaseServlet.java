package com.eventcart.controller;

import com.eventcart.dto.ApiResponse;
import com.eventcart.entity.User;
import com.eventcart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Abstract BaseServlet providing reusable utility methods for MVC controllers.
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Forwards request and response to a JSP view under WEB-INF.
     */
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String jspPath)
            throws ServletException, IOException {
        String view = jspPath.startsWith("/") ? jspPath : "/WEB-INF/views/" + jspPath;
        if (!view.endsWith(".jsp")) {
            view += ".jsp";
        }
        req.getRequestDispatcher(view).forward(req, resp);
    }

    /**
     * Redirects browser to a context-relative URI.
     */
    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        String destination = path.startsWith("/") ? req.getContextPath() + path : req.getContextPath() + "/" + path;
        resp.sendRedirect(destination);
    }

    /**
     * Writes a JSON payload to the HTTP response stream with UTF-8 encoding.
     */
    protected void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(JsonUtil.toJson(data));
            writer.flush();
        }
    }

    /**
     * Writes an ApiResponse with 200 OK to the client.
     */
    protected void sendJsonOk(HttpServletResponse resp, String message, Object data) throws IOException {
        sendJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(message, data));
    }

    /**
     * Writes an ApiResponse with 400 Bad Request to the client.
     */
    protected void sendJsonError(HttpServletResponse resp, int status, String message) throws IOException {
        sendJson(resp, status, ApiResponse.fail(message));
    }

    /**
     * Extracts currently logged-in user from the session, if present.
     */
    protected User getAuthenticatedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("authUser");
        }
        return null;
    }

    /**
     * Safely reads an integer parameter with a fallback default value.
     */
    protected int getIntParameter(HttpServletRequest req, String paramName, int defaultValue) {
        String val = req.getParameter(paramName);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely reads a string parameter with a fallback default value.
     */
    protected String getStringParameter(HttpServletRequest req, String paramName, String defaultValue) {
        String val = req.getParameter(paramName);
        return (val != null && !val.trim().isEmpty()) ? val.trim() : defaultValue;
    }

    /**
     * Sets a flash message in session for user feedback on next page view.
     */
    protected void setFlash(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession(true);
        session.setAttribute("flash_" + type, message);
    }
}
