<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Your Shopping Cart | EventCart" scope="request" />
<c:set var="pageActive" value="cart" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="py-4">
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb small">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/" class="text-decoration-none">Home</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/events" class="text-decoration-none">Events</a></li>
                <li class="breadcrumb-item active" aria-current="page">Shopping Cart</li>
            </ol>
        </nav>

        <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

        <!-- Cart Inventory Warnings if any -->
        <c:if test="${not empty cartValidation and not empty cartValidation.warningMessages}">
            <div class="alert alert-warning border-0 shadow-sm rounded-3 mb-4">
                <div class="d-flex">
                    <i class="bi bi-exclamation-triangle-fill fs-5 me-2"></i>
                    <div>
                        <strong>Notice:</strong>
                        <ul class="mb-0 ps-3">
                            <c:forEach var="warn" items="${cartValidation.warningMessages}">
                                <li><c:out value="${warn}" /></li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </c:if>

        <h1 class="fw-bold mb-4 text-slate-900 d-flex align-items-center">
            <i class="bi bi-bag-check me-2 text-primary"></i> Shopping Cart
            <span class="badge bg-light text-primary border ms-3 fs-6" id="cart-item-count-badge">
                <c:out value="${cart.totalQuantity}" /> <c:out value="${cart.totalQuantity == 1 ? 'ticket' : 'tickets'}" />
            </span>
        </h1>

        <!-- Empty State Container -->
        <div id="cart-empty-state" class="${cart.empty ? '' : 'd-none'}">
            <div class="card border-0 shadow-sm rounded-4 p-5 text-center bg-white my-4">
                <div class="mb-3">
                    <span class="rounded-circle bg-light p-4 d-inline-block">
                        <i class="bi bi-cart-x fs-1 text-muted"></i>
                    </span>
                </div>
                <h3 class="fw-bold text-dark mb-2">Your Cart is Empty</h3>
                <p class="text-muted mx-auto mb-4" style="max-width: 480px;">
                    Discover exciting concerts, tech conferences, theater performances, and sports matches happening near you.
                </p>
                <div>
                    <a href="${pageContext.request.contextPath}/events" class="btn btn-primary-event px-4 py-2">
                        <i class="bi bi-compass me-1"></i> Explore Events
                    </a>
                </div>
            </div>
        </div>

        <!-- Populated Cart Container -->
        <div id="cart-populated-container" class="${cart.empty ? 'd-none' : ''}">
            <div class="row g-4">
                <!-- Left Column: Item List Table -->
                <div class="col-lg-8">
                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden bg-white mb-3">
                        <div class="table-responsive">
                            <table class="table align-middle mb-0" id="cart-table">
                                <thead class="table-light text-muted small text-uppercase">
                                    <tr>
                                        <th scope="col" class="ps-4 py-3">Event & Ticket</th>
                                        <th scope="col" class="py-3 text-center">Unit Price</th>
                                        <th scope="col" class="py-3 text-center" style="width: 140px;">Quantity</th>
                                        <th scope="col" class="py-3 text-end">Subtotal</th>
                                        <th scope="col" class="pe-4 py-3 text-center" style="width: 60px;">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${cart.itemsList}">
                                        <tr id="cart-row-${item.ticketTypeId}" class="border-bottom">
                                            <!-- Event details & banner -->
                                            <td class="ps-4 py-3">
                                                <div class="d-flex align-items-center">
                                                    <div class="flex-shrink-0 me-3 rounded-3 overflow-hidden bg-light border" style="width: 68px; height: 50px;">
                                                        <c:choose>
                                                            <c:when test="${not empty item.bannerImage}">
                                                                <img src="${pageContext.request.contextPath}/${item.bannerImage}" alt="${item.eventTitle}"
                                                                     class="w-100 h-100 object-fit-cover">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="w-100 h-100 d-flex align-items-center justify-content-center text-muted">
                                                                    <i class="bi bi-ticket-perforated fs-4"></i>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div>
                                                        <h6 class="fw-bold mb-1">
                                                            <a href="${pageContext.request.contextPath}/event?id=${item.eventId}" class="text-decoration-none text-dark">
                                                                <c:out value="${item.eventTitle}" />
                                                            </a>
                                                        </h6>
                                                        <div class="d-flex flex-wrap align-items-center gap-2 small">
                                                            <span class="badge bg-primary-subtle text-primary border border-primary-subtle">
                                                                <c:out value="${item.ticketTypeName}" />
                                                            </span>
                                                            <span class="text-muted">
                                                                <i class="bi bi-geo-alt me-1"></i><c:out value="${item.venue}" />
                                                            </span>
                                                            <c:if test="${not empty item.eventDate}">
                                                                <span class="text-muted">&bull; <c:out value="${item.eventDate}" /></span>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>

                                            <!-- Unit Price -->
                                            <td class="text-center py-3">
                                                <span class="text-muted fw-semibold">$${item.unitPrice}</span>
                                            </td>

                                            <!-- Quantity Stepper -->
                                            <td class="py-3 text-center">
                                                <div class="input-group input-group-sm mx-auto" style="width: 105px;">
                                                    <button class="btn btn-outline-secondary" type="button" aria-label="Decrease quantity"
                                                            onclick="changeCartQty(${item.ticketTypeId}, -1)">&minus;</button>
                                                    <input type="number" class="form-control text-center px-1" id="cart-qty-input-${item.ticketTypeId}"
                                                           value="${item.quantity}" min="1" max="10" readonly
                                                           aria-label="Quantity of ${item.ticketTypeName}">
                                                    <button class="btn btn-outline-secondary" type="button" aria-label="Increase quantity"
                                                            onclick="changeCartQty(${item.ticketTypeId}, 1)">&plus;</button>
                                                </div>
                                            </td>

                                            <!-- Subtotal -->
                                            <td class="text-end py-3 fw-bold text-dark">
                                                <span id="item-subtotal-${item.ticketTypeId}">$${item.subtotal}</span>
                                            </td>

                                            <!-- Remove action -->
                                            <td class="pe-4 py-3 text-center">
                                                <button type="button" class="btn btn-outline-danger btn-sm border-0 rounded-circle"
                                                        title="Remove from cart" aria-label="Remove ticket"
                                                        onclick="removeCartItem(${item.ticketTypeId})">
                                                    <i class="bi bi-trash3"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Card Footer: Continue & Clear actions -->
                        <div class="card-footer bg-white border-0 py-3 px-4 d-flex justify-content-between align-items-center flex-wrap gap-2">
                            <a href="${pageContext.request.contextPath}/events" class="btn btn-outline-secondary btn-sm">
                                <i class="bi bi-arrow-left me-1"></i> Continue Shopping
                            </a>
                            <button type="button" class="btn btn-outline-danger btn-sm" onclick="clearFullCart()">
                                <i class="bi bi-x-circle me-1"></i> Clear Cart
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Right Column: Order Summary Card -->
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm rounded-4 p-4 bg-white sticky-top" style="top: 90px; z-index: 10;">
                        <h5 class="fw-bold mb-3 text-slate-900">Order Summary</h5>

                        <ul class="list-unstyled mb-4 small">
                            <li class="py-2 border-bottom d-flex justify-content-between">
                                <span class="text-muted">Tickets Subtotal</span>
                                <span class="fw-semibold text-dark" id="summary-subtotal">$${cart.totalAmount}</span>
                            </li>
                            <li class="py-2 border-bottom d-flex justify-content-between">
                                <span class="text-muted">Service & Facility Fee</span>
                                <span class="fw-semibold text-success">$0.00 (Waived)</span>
                            </li>
                            <li class="py-3 d-flex justify-content-between align-items-center">
                                <span class="fw-bold text-dark fs-5">Estimated Total</span>
                                <span class="fw-bold text-primary fs-4" id="summary-total">$${cart.totalAmount}</span>
                            </li>
                        </ul>

                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary-event w-100 py-3 fw-bold mb-3 d-flex align-items-center justify-content-center gap-2">
                            <span>Proceed to Checkout</span>
                            <i class="bi bi-arrow-right"></i>
                        </a>

                        <!-- Security and Purchase Guarantee -->
                        <div class="p-3 bg-light rounded-3 border small">
                            <div class="fw-semibold text-dark mb-1">
                                <i class="bi bi-shield-lock-fill me-1 text-primary"></i> Guaranteed Booking Protection
                            </div>
                            <p class="text-muted mb-0">Seat inventory will be atomically locked during checkout reservation.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
    async function changeCartQty(ticketId, delta) {
        const input = document.getElementById('cart-qty-input-' + ticketId);
        if (!input) return;
        let currentVal = parseInt(input.value, 10);
        if (isNaN(currentVal)) currentVal = 1;

        let targetVal = currentVal + delta;
        if (targetVal < 1) {
            removeCartItem(ticketId);
            return;
        }
        if (targetVal > 10) {
            EventCart.showNotification('Maximum 10 tickets per tier allowed.', 'warning');
            return;
        }

        try {
            const formData = new URLSearchParams();
            formData.append('ticketTypeId', ticketId);
            formData.append('quantity', targetVal);

            const res = await EventCart.request('${pageContext.request.contextPath}/cart/update', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (res && res.success) {
                input.value = res.data.quantity;
                const subtotalEl = document.getElementById('item-subtotal-' + ticketId);
                if (subtotalEl) {
                    subtotalEl.textContent = '$' + Number(res.data.itemSubtotal).toFixed(2);
                }
                updateOrderSummary(res.data.totalAmount, res.data.cartCount);
                EventCart.updateCartBadge(res.data.cartCount);
            } else {
                EventCart.showNotification(res.message || 'Could not update ticket quantity.', 'error');
            }
        } catch (err) {
            EventCart.showNotification(err.message || 'Failed to update quantity.', 'error');
        }
    }

    async function removeCartItem(ticketId) {
        try {
            const formData = new URLSearchParams();
            formData.append('ticketTypeId', ticketId);

            const res = await EventCart.request('${pageContext.request.contextPath}/cart/remove', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (res && res.success) {
                const row = document.getElementById('cart-row-' + ticketId);
                if (row) {
                    row.remove();
                }
                updateOrderSummary(res.data.totalAmount, res.data.cartCount);
                EventCart.updateCartBadge(res.data.cartCount);
                EventCart.showNotification('Ticket removed from cart.', 'info');

                if (res.data.isEmpty) {
                    showEmptyCartState();
                }
            } else {
                EventCart.showNotification(res.message || 'Could not remove ticket.', 'error');
            }
        } catch (err) {
            EventCart.showNotification(err.message || 'Failed to remove ticket.', 'error');
        }
    }

    async function clearFullCart() {
        if (!confirm('Are you sure you want to clear your entire cart?')) {
            return;
        }

        try {
            const res = await EventCart.request('${pageContext.request.contextPath}/cart/clear', {
                method: 'POST'
            });

            if (res && res.success) {
                EventCart.updateCartBadge(0);
                showEmptyCartState();
                EventCart.showNotification('Your shopping cart has been cleared.', 'info');
            }
        } catch (err) {
            EventCart.showNotification(err.message || 'Failed to clear cart.', 'error');
        }
    }

    function updateOrderSummary(totalAmount, count) {
        const formatted = '$' + Number(totalAmount).toFixed(2);
        const subtotalEl = document.getElementById('summary-subtotal');
        const totalEl = document.getElementById('summary-total');
        const badgeEl = document.getElementById('cart-item-count-badge');

        if (subtotalEl) subtotalEl.textContent = formatted;
        if (totalEl) totalEl.textContent = formatted;
        if (badgeEl) badgeEl.textContent = count + (count === 1 ? ' ticket' : ' tickets');
    }

    function showEmptyCartState() {
        const populated = document.getElementById('cart-populated-container');
        const empty = document.getElementById('cart-empty-state');
        if (populated) populated.classList.add('d-none');
        if (empty) empty.classList.remove('d-none');
        const badgeEl = document.getElementById('cart-item-count-badge');
        if (badgeEl) badgeEl.textContent = '0 tickets';
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
