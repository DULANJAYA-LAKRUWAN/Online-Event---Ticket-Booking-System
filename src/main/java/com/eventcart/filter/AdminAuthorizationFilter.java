package com.eventcart.filter;

import com.eventcart.entity.Role;
import com.eventcart.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Authorization filter ensuring only users with Role.ADMIN can access administrative endpoints.
 */
@WebFilter(filterName = "AdminAuthorizationFilter", urlPatterns = {"/admin/*"})
public class AdminAuthorizationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AdminAuthorizationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AdminAuthorizationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User authUser = (session != null) ? (User) session.getAttribute("authUser") : null;

        if (authUser == null) {
            // Let AuthenticationFilter handle or redirect to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (authUser.getRole() == Role.ADMIN) {
            // Full administrative privileges across all admin routes
            chain.doFilter(request, response);
        } else if (authUser.getRole() == Role.ORGANIZER && path.startsWith("/admin/events")) {
            // Organizers are authorized to manage event listings and ticket tiers
            chain.doFilter(request, response);
        } else {
            // Insufficient permissions -> 403 Forbidden
            logger.warn("Access denied: User '{}' with role '{}' tried to access admin route '{}'",
                    authUser.getEmail(), authUser.getRole(), httpRequest.getRequestURI());
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Administrative privileges required.");
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
