<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Checkout & Ticket Reservation | EventCart" scope="request" />
<c:set var="pageActive" value="checkout" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="py-4">
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb small">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/" class="text-decoration-none">Home</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/cart" class="text-decoration-none">Cart</a></li>
                <li class="breadcrumb-item active" aria-current="page">Checkout & Reservation</li>
            </ol>
        </nav>

        <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

        <!-- Checkout Step Progress Indicator -->
        <div class="d-flex align-items-center justify-content-center mb-4">
            <div class="d-flex align-items-center gap-3 bg-white p-3 rounded-4 shadow-sm border">
                <div class="d-flex align-items-center text-success">
                    <span class="badge bg-success-subtle text-success border border-success-subtle rounded-circle p-2 me-2">
                        <i class="bi bi-cart-check"></i>
                    </span>
                    <span class="small fw-semibold">1. Shopping Cart</span>
                </div>
                <i class="bi bi-chevron-right text-muted small"></i>
                <div class="d-flex align-items-center text-primary">
                    <span class="badge bg-primary text-white rounded-circle p-2 me-2">2</span>
                    <span class="small fw-bold">2. Reservation & Details</span>
                </div>
                <i class="bi bi-chevron-right text-muted small"></i>
                <div class="d-flex align-items-center text-muted opacity-75">
                    <span class="badge bg-light text-muted border rounded-circle p-2 me-2">3</span>
                    <span class="small">3. Payment & Issuance</span>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- Left Column: Customer Details & Policy Confirmation -->
            <div class="col-lg-7">
                <!-- Customer Information Card -->
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white mb-4">
                    <h5 class="fw-bold mb-3 text-slate-900 d-flex align-items-center">
                        <i class="bi bi-person-badge me-2 text-primary"></i> Customer Information
                    </h5>
                    <p class="text-muted small mb-3">
                        Tickets and authentication reference codes will be issued to this customer account.
                    </p>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Full Name</label>
                            <input type="text" class="form-control bg-light" value="${checkout.fullName}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Email Address</label>
                            <input type="email" class="form-control bg-light" value="${checkout.email}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Contact Phone</label>
                            <input type="text" class="form-control bg-light" value="${not empty checkout.phoneNumber ? checkout.phoneNumber : 'Not provided'}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label small fw-semibold text-secondary">Account Status</label>
                            <div class="form-control bg-light text-success fw-semibold">
                                <i class="bi bi-check-circle-fill me-1"></i> Verified Customer
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Reservation Architecture Disclosure (Academic Criteria) -->
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white">
                    <h5 class="fw-bold mb-3 text-slate-900 d-flex align-items-center">
                        <i class="bi bi-shield-lock me-2 text-primary"></i> Real-Time Inventory Locking
                    </h5>
                    <div class="d-flex gap-3 text-muted small">
                        <i class="bi bi-info-circle-fill text-primary fs-4 flex-shrink-0"></i>
                        <div>
                            <p class="mb-2">
                                When you confirm your reservation, EventCart executes a database transaction with
                                <strong>pessimistic row locking</strong> (<code>SELECT ... FOR UPDATE</code>) on MySQL 8.
                            </p>
                            <p class="mb-0">
                                This guarantees that your requested ticket quantities are atomically reserved without risk of overselling or race conditions.
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Column: Order Summary & Place Reservation Button -->
            <div class="col-lg-5">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white sticky-top" style="top: 90px; z-index: 10;">
                    <h5 class="fw-bold mb-3 text-slate-900">Booking Summary</h5>

                    <!-- Itemized summary list -->
                    <div class="d-flex flex-column gap-3 mb-4">
                        <c:forEach var="item" items="${checkout.cart.itemsList}">
                            <div class="p-3 bg-light rounded-3 border">
                                <div class="d-flex justify-content-between align-items-start">
                                    <div>
                                        <h6 class="fw-bold mb-1 text-dark text-truncate" style="max-width: 200px;" title="${item.eventTitle}">
                                            <c:out value="${item.eventTitle}" />
                                        </h6>
                                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle small">
                                            <c:out value="${item.ticketTypeName}" /> &times; ${item.quantity}
                                        </span>
                                    </div>
                                    <span class="fw-bold text-dark">$${item.subtotal}</span>
                                </div>
                                <div class="text-muted small mt-2 d-flex justify-content-between">
                                    <span>$${item.unitPrice} each</span>
                                    <span>${item.venue}</span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pricing breakdown -->
                    <ul class="list-unstyled mb-4 small">
                        <li class="py-2 border-bottom d-flex justify-content-between">
                            <span class="text-muted">Tickets Subtotal</span>
                            <span class="fw-semibold text-dark">$${checkout.subtotal}</span>
                        </li>
                        <li class="py-2 border-bottom d-flex justify-content-between">
                            <span class="text-muted">Special University / Early Discount</span>
                            <span class="fw-semibold text-success">-$${checkout.discountAmount}</span>
                        </li>
                        <li class="py-3 d-flex justify-content-between align-items-center">
                            <span class="fw-bold text-dark fs-5">Final Reservation Total</span>
                            <span class="fw-bold text-primary fs-3">$${checkout.totalAmount}</span>
                        </li>
                    </ul>

                    <!-- Action Button Form -->
                    <form id="checkout-form" method="post" action="${pageContext.request.contextPath}/checkout">
                        <button type="submit" id="btn-submit-checkout" class="btn btn-primary-event w-100 py-3 fw-bold fs-6 shadow-sm">
                            <i class="bi bi-lock-fill me-1"></i> Confirm &amp; Reserve Tickets
                        </button>
                    </form>

                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/cart" class="text-muted small text-decoration-none">
                            <i class="bi bi-arrow-left me-1"></i> Return to Shopping Cart
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
    document.getElementById('checkout-form').addEventListener('submit', async function (e) {
        e.preventDefault();

        const btn = document.getElementById('btn-submit-checkout');
        const originalHtml = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Reserving Inventory...';

        try {
            const res = await EventCart.request('${pageContext.request.contextPath}/checkout', {
                method: 'POST'
            });

            if (res && res.success && res.data && res.data.redirectUrl) {
                EventCart.updateCartBadge(0);
                window.location.href = res.data.redirectUrl;
            } else {
                EventCart.showNotification(res.message || 'Reservation failed.', 'error');
                btn.disabled = false;
                btn.innerHTML = originalHtml;
            }
        } catch (err) {
            EventCart.showNotification(err.message || 'Error processing reservation.', 'error');
            btn.disabled = false;
            btn.innerHTML = originalHtml;
        }
    });
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
