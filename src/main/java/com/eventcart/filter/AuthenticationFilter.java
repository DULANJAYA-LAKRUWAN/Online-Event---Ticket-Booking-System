package com.eventcart.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Authentication filter guarding private and user-specific endpoints.
 * Whitelists public assets and authentication endpoints.
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {
        "/profile/*",
        "/account/*",
        "/checkout/*",
        "/bookings/*",
        "/my-tickets/*",
        "/organizer/*",
        "/admin/*"
})
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    // Explicit public prefixes allowed even if caught by a broader pattern
    private static final List<String> PUBLIC_EXTENSIONS = Arrays.asList(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".svg", ".ico", ".woff2"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Always allow static resources
        for (String ext : PUBLIC_EXTENSIONS) {
            if (path.endsWith(ext)) {
                chain.doFilter(request, response);
                return;
            }
        }

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("authUser") != null);

        if (isLoggedIn) {
            // User is authenticated, proceed to the requested protected resource
            chain.doFilter(request, response);
        } else {
            // Not authenticated: capture target URI and redirect to login
            logger.debug("Unauthenticated access attempt to '{}'. Redirecting to login.", path);
            String redirectUrl = httpRequest.getRequestURI();
            String queryString = httpRequest.getQueryString();
            if (queryString != null && !queryString.trim().isEmpty()) {
                redirectUrl += "?" + queryString;
            }
            String encodedTarget = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);

            // Set a user-friendly notice in session if available
            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute("flash_warning", "Please sign in to access this page.");

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?returnUrl=" + encodedTarget);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
