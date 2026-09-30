<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create an Account | EventCart" scope="request" />
<c:set var="pageActive" value="register" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="auth-wrapper">
    <div class="auth-card auth-card-wide">
        <div class="auth-card-header">
            <span class="navbar-brand-icon mb-2">
                <i class="bi bi-person-plus-fill"></i>
            </span>
            <h3 class="fw-bold mb-1">Create Your Account</h3>
            <p class="small text-secondary mb-0">Join EventCart to discover events or host your own experiences</p>
        </div>

        <div class="auth-card-body">
            <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

            <form action="${pageContext.request.contextPath}/register" method="post" class="needs-validation" novalidate id="registerForm">
                <div class="row g-3">
                    <!-- Full Name -->
                    <div class="col-md-12">
                        <label for="fullName" class="form-label">Full Name</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-person"></i></span>
                            <input type="text" class="form-control" id="fullName" name="fullName"
                                   value="<c:out value='${requestScope.fullName}' />"
                                   placeholder="e.g. John Doe" required autocomplete="name">
                            <div class="invalid-feedback">Full name is required.</div>
                        </div>
                    </div>

                    <!-- Email Address -->
                    <div class="col-md-6">
                        <label for="email" class="form-label">Email Address</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="<c:out value='${requestScope.email}' />"
                                   placeholder="name@example.com" required autocomplete="email">
                            <div class="invalid-feedback">Valid email address is required.</div>
                        </div>
                    </div>

                    <!-- Phone Number -->
                    <div class="col-md-6">
                        <label for="phoneNumber" class="form-label">Phone Number</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-telephone"></i></span>
                            <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber"
                                   value="<c:out value='${requestScope.phoneNumber}' />"
                                   placeholder="+1 234 567 8900" autocomplete="tel">
                        </div>
                    </div>

                    <!-- Role Selection -->
                    <div class="col-md-12">
                        <label for="role" class="form-label">Account Purpose</label>
                        <select class="form-select" id="role" name="role" required>
                            <option value="CUSTOMER" ${requestScope.role == 'CUSTOMER' || empty requestScope.role ? 'selected' : ''}>
                                Attendee (Book &amp; Attend Events)
                            </option>
                            <option value="ORGANIZER" ${requestScope.role == 'ORGANIZER' ? 'selected' : ''}>
                                Event Organizer (Publish Events &amp; Sell Tickets)
                            </option>
                        </select>
                    </div>

                    <!-- Password -->
                    <div class="col-md-6">
                        <label for="password" class="form-label">Password</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-lock"></i></span>
                            <input type="password" class="form-control" id="password" name="password"
                                   minlength="6" placeholder="At least 6 characters" required autocomplete="new-password">
                            <div class="invalid-feedback">Password must be at least 6 characters.</div>
                        </div>
                    </div>

                    <!-- Confirm Password -->
                    <div class="col-md-6">
                        <label for="confirmPassword" class="form-label">Confirm Password</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-muted"><i class="bi bi-shield-check"></i></span>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                                   minlength="6" placeholder="Re-enter password" required autocomplete="new-password">
                            <div class="invalid-feedback" id="confirmFeedback">Passwords must match.</div>
                        </div>
                    </div>

                    <!-- Terms agreement -->
                    <div class="col-12">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="agreeTerms" required>
                            <label class="form-check-label small text-secondary" for="agreeTerms">
                                I agree to the <a href="#" class="text-primary text-decoration-none">Terms of Service</a> and <a href="#" class="text-primary text-decoration-none">Privacy Policy</a>
                            </label>
                            <div class="invalid-feedback">You must agree to the terms before submitting.</div>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary-event w-100 py-2 mt-4 mb-3" id="submitRegisterBtn">
                    <i class="bi bi-person-check me-1"></i> Complete Registration
                </button>
            </form>

            <div class="text-center small text-secondary">
                Already registered?
                <a href="${pageContext.request.contextPath}/login" class="fw-semibold text-primary text-decoration-none">
                    Sign in here
                </a>
            </div>
        </div>
    </div>
</main>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const form = document.getElementById('registerForm');
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');

        form.addEventListener('submit', (e) => {
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity("Passwords do not match");
            } else {
                confirmPassword.setCustomValidity("");
            }

            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
            }
            form.classList.add('was-validated');
        });

        confirmPassword.addEventListener('input', () => {
            if (password.value === confirmPassword.value) {
                confirmPassword.setCustomValidity("");
            } else {
                confirmPassword.setCustomValidity("Passwords do not match");
            }
        });
    });
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
