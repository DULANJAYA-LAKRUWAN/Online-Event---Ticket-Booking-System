<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    // Generate a random incident reference code for logging without exposing internal stack traces
    String errorRef = "ERR-" + Long.toHexString(System.currentTimeMillis()).toUpperCase();
    if (exception != null) {
        org.slf4j.LoggerFactory.getLogger("com.eventcart.GlobalErrorHandler")
                .error("Server exception [Ref: {}]: {}", errorRef, exception.getMessage(), exception);
    }
    request.setAttribute("errorRef", errorRef);
%>
<c:set var="pageTitle" value="500 - Server Error | EventCart" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-5">
    <div class="error-card">
        <div class="error-code-badge text-danger">500</div>
        <h2 class="fw-bold mb-3">Something Went Wrong</h2>
        <p class="text-muted mb-2">
            An unexpected error occurred while processing your request. Our engineering team has been notified.
        </p>
        <p class="small text-secondary mb-4">
            Reference Incident Code: <code class="bg-light px-2 py-1 rounded text-dark"><c:out value="${errorRef}" /></code>
        </p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary-event">
                <i class="bi bi-house-door me-1"></i> Return Home
            </a>
            <a href="javascript:location.reload()" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-clockwise me-1"></i> Try Again
            </a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
