<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="404 - Page Not Found | EventCart" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-5">
    <div class="error-card">
        <div class="error-code-badge text-primary">404</div>
        <h2 class="fw-bold mb-3">Event or Page Not Found</h2>
        <p class="text-muted mb-4">
            The page, ticket, or event you are searching for might have been moved, expired, or does not exist.
        </p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary-event">
                <i class="bi bi-house-door me-1"></i> Return to Home
            </a>
            <a href="${pageContext.request.contextPath}/#featured-events" class="btn btn-outline-secondary">
                <i class="bi bi-search me-1"></i> Browse Events
            </a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
