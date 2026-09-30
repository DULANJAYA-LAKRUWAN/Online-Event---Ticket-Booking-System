<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="401 - Unauthorized | EventCart" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-5">
    <div class="error-card">
        <div class="error-code-badge text-info">401</div>
        <h2 class="fw-bold mb-3">Authentication Required</h2>
        <p class="text-muted mb-4">
            You must be logged in with valid credentials to access the requested resource.
        </p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary-event">
                <i class="bi bi-box-arrow-in-right me-1"></i> Sign In Now
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                <i class="bi bi-house-door me-1"></i> Return Home
            </a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
