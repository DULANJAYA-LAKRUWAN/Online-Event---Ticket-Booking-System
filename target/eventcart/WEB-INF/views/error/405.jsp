<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="405 - Method Not Allowed | EventCart" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-5">
    <div class="error-card">
        <div class="error-code-badge text-secondary">405</div>
        <h2 class="fw-bold mb-3">Method Not Allowed</h2>
        <p class="text-muted mb-4">
            The requested HTTP method is not supported for this URL endpoint.
        </p>
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary-event">
                <i class="bi bi-house-door me-1"></i> Return Home
            </a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
