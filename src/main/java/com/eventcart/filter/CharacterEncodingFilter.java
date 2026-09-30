package com.eventcart.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Global filter enforcing UTF-8 character encoding on all incoming requests and outgoing responses.
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = "/*", initParams = {
        @WebInitParam(name = "encoding", value = "UTF-8"),
        @WebInitParam(name = "forceEncoding", value = "true")
})
public class CharacterEncodingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CharacterEncodingFilter.class);
    private String encoding = "UTF-8";
    private boolean forceEncoding = true;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configuredEncoding = filterConfig.getInitParameter("encoding");
        if (configuredEncoding != null && !configuredEncoding.trim().isEmpty()) {
            this.encoding = configuredEncoding.trim();
        }
        String configuredForce = filterConfig.getInitParameter("forceEncoding");
        if (configuredForce != null) {
            this.forceEncoding = Boolean.parseBoolean(configuredForce);
        }
        logger.info("CharacterEncodingFilter initialized with encoding: {}, force: {}", this.encoding, this.forceEncoding);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            if (this.forceEncoding || httpRequest.getCharacterEncoding() == null) {
                httpRequest.setCharacterEncoding(this.encoding);
            }
            if (this.forceEncoding || httpResponse.getCharacterEncoding() == null) {
                httpResponse.setCharacterEncoding(this.encoding);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
