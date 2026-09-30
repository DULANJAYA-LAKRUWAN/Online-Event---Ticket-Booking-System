<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign In | EventCart" scope="request" />
<c:set var="pageActive" value="login" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-card-header">
            <span class="navbar-brand-icon mb-2">
                <i class="bi bi-person-fill-lock"></i>
            </span>
            <h3 class="fw-bold mb-1">Welcome Back</h3>
            <p class="small text-secondary mb-0">Sign in to access your booked tickets and dashboard</p>
        </div>

        <div class="auth-card-body">
            <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

            <c:if test="${param.registered == 'true'}">
                <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i> Registration successful! Please log in with your credentials.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${param.logout == 'true'}">
                <div class="alert alert-info alert-dismissible fade show border-0 shadow-sm" role="alert">
                    <i class="bi bi-info-circle-fill me-2"></i> You have been successfully logged out.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post" class="needs-validation" novalidate id="loginForm">
                <c:if test="${not empty requestScope.returnUrl}">
                    <input type="hidden" name="returnUrl" value="<c:out value='${requestScope.returnUrl}' />">
                </c:if>

                <div class="mb-3">
                    <label for="email" class="form-label">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-muted"><i class="bi bi-envelope"></i></span>
                        <input type="email" class="form-control" id="email" name="email"
                               value="<c:out value='${requestScope.email}' />"
                               placeholder="name@example.com" required autocomplete="email">
                        <div class="invalid-feedback">Please enter a valid email address.</div>
                    </div>
                </div>

                <div class="mb-3">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <label for="password" class="form-label mb-0">Password</label>
                        <a href="#" class="small text-decoration-none text-primary" onclick="alert('Password reset link will be sent to your email.'); return false;">Forgot?</a>
                    </div>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-muted"><i class="bi bi-key"></i></span>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Enter your password" required autocomplete="current-password">
                        <button class="btn btn-outline-secondary" type="button" id="togglePasswordBtn">
                            <i class="bi bi-eye" id="togglePasswordIcon"></i>
                        </button>
                        <div class="invalid-feedback">Password is required.</div>
                    </div>
                </div>

                <div class="mb-4 form-check">
                    <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
                    <label class="form-check-label small text-secondary" for="rememberMe">Remember my session</label>
                </div>

                <button type="submit" class="btn btn-primary-event w-100 py-2 mb-3">
                    <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                </button>
            </form>

            <!-- Test Credentials Hint Box for University Examiner -->
            <div class="p-3 bg-light rounded-3 border small mb-3">
                <div class="fw-semibold text-secondary mb-1">
                    <i class="bi bi-info-circle me-1 text-primary"></i> Academic Test Credentials:
                </div>
                <div class="text-muted">
                    <code>admin@eventcart.com</code> / <code>Admin@123</code> (Admin)<br>
                    <code>organizer@eventcart.com</code> / <code>Organizer@123</code> (Organizer)<br>
                    <code>user@eventcart.com</code> / <code>User@123</code> (Customer)
                </div>
            </div>

            <div class="text-center small text-secondary">
                Don't have an account yet?
                <a href="${pageContext.request.contextPath}/register" class="fw-semibold text-primary text-decoration-none">
                    Create an account
                </a>
            </div>
        </div>
    </div>
</main>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        // Toggle password visibility
        const toggleBtn = document.getElementById('togglePasswordBtn');
        const passwordInput = document.getElementById('password');
        const icon = document.getElementById('togglePasswordIcon');
        if (toggleBtn && passwordInput) {
            toggleBtn.addEventListener('click', () => {
                const isPassword = passwordInput.getAttribute('type') === 'password';
                passwordInput.setAttribute('type', isPassword ? 'text' : 'password');
                icon.className = isPassword ? 'bi bi-eye-slash' : 'bi bi-eye';
            });
        }

        // Form client-side validation
        const form = document.getElementById('loginForm');
        form.addEventListener('submit', (e) => {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
