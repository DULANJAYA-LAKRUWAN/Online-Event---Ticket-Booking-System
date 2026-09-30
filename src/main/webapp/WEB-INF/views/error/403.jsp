<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="403 - Access Denied | EventCart" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-5">
    <div class="error-card">
        <div class="error-code-badge text-danger">403</div>
        <h2 class="fw-bold mb-3">Access Denied</h2>
        <p class="text-muted mb-4">
            You do not possess the necessary privileges or permissions to access this area.
        </p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary-event">
                <i class="bi bi-house-door me-1"></i> Return Home
            </a>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary">
                <i class="bi bi-person-badge me-1"></i> Switch Account
            </a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
