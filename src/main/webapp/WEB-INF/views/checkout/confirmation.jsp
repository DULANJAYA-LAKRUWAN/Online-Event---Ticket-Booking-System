<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reservation Confirmed | EventCart" scope="request" />
<c:set var="pageActive" value="checkout" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="py-5">
    <div class="container" style="max-width: 820px;">
        <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

        <!-- Success Header Card -->
        <div class="card border-0 shadow-sm rounded-4 p-5 text-center bg-white mb-4">
            <div class="mb-3">
                <span class="rounded-circle bg-success-subtle p-3 d-inline-block text-success">
                    <i class="bi bi-check-circle-fill fs-1"></i>
                </span>
            </div>

            <h2 class="fw-bold text-slate-900 mb-2">Reservation Confirmed!</h2>
            <p class="text-muted mb-4">
                Your ticket inventory has been safely secured and locked in the database under transaction.
            </p>

            <!-- Reference & Status Pill -->
            <div class="d-flex flex-wrap align-items-center justify-content-center gap-3 p-3 bg-light rounded-4 border mx-auto" style="max-width: 540px;">
                <div>
                    <span class="text-muted small d-block">Booking Reference</span>
                    <strong class="fs-5 text-primary tracking-wide font-monospace">${booking.bookingReference}</strong>
                </div>
                <div class="vr d-none d-sm-block"></div>
                <div>
                    <span class="text-muted small d-block">Reservation Status</span>
                    <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle px-3 py-2 fs-6">
                        <i class="bi bi-hourglass-split me-1"></i> ${booking.status}
                    </span>
                </div>
                <div class="vr d-none d-sm-block"></div>
                <div>
                    <span class="text-muted small d-block">Total Reserved</span>
                    <strong class="fs-5 text-dark">$${booking.finalAmount}</strong>
                </div>
            </div>
        </div>

        <!-- Reserved Items Details -->
        <div class="card border-0 shadow-sm rounded-4 p-4 bg-white mb-4">
            <h5 class="fw-bold mb-3 text-slate-900 d-flex align-items-center">
                <i class="bi bi-ticket-detailed me-2 text-primary"></i> Reserved Ticket Items
            </h5>

            <div class="table-responsive">
                <table class="table align-middle mb-0">
                    <thead class="table-light text-muted small text-uppercase">
                        <tr>
                            <th class="ps-3 py-2">Event &amp; Ticket Tier</th>
                            <th class="text-center py-2">Quantity</th>
                            <th class="text-end py-2">Unit Price</th>
                            <th class="text-end pe-3 py-2">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${booking.items}">
                            <tr>
                                <td class="ps-3 py-3">
                                    <div class="fw-bold text-dark">
                                        <c:out value="${item.ticketType.event.title}" />
                                    </div>
                                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle small">
                                        <c:out value="${item.ticketType.name}" />
                                    </span>
                                    <span class="text-muted small ms-2">
                                        <i class="bi bi-geo-alt me-1"></i><c:out value="${item.ticketType.event.venue}" />
                                    </span>
                                </td>
                                <td class="text-center py-3 fw-semibold">
                                    ${item.quantity}
                                </td>
                                <td class="text-end py-3 text-muted">
                                    $${item.unitPrice}
                                </td>
                                <td class="text-end pe-3 py-3 fw-bold text-dark">
                                    $${item.subtotal}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot class="table-light">
                        <tr>
                            <td colspan="3" class="ps-3 py-3 fw-bold text-dark">Total Amount Due</td>
                            <td class="text-end pe-3 py-3 fw-bold fs-5 text-primary">$${booking.finalAmount}</td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>

        <!-- Phase 04 Payment Integration Notice -->
        <div class="alert alert-info border-0 shadow-sm rounded-4 p-4 mb-4">
            <div class="d-flex">
                <i class="bi bi-credit-card-2-front fs-3 me-3 text-primary flex-shrink-0"></i>
                <div>
                    <h6 class="fw-bold mb-1 text-slate-900">Next Step: Phase 04 Payment Gateway</h6>
                    <p class="small mb-0 text-secondary">
                        The checkout foundation and inventory locking are now complete. In Phase 04, the Mock Payment Gateway
                        will authenticate transactions, generate secure QR ticket codes, and dispatch confirmation receipts.
                    </p>
                </div>
            </div>
        </div>

        <!-- Navigation Buttons -->
        <div class="d-flex justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/events" class="btn btn-outline-secondary px-4 py-2">
                <i class="bi bi-compass me-1"></i> Browse More Events
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary-event px-4 py-2">
                <i class="bi bi-house me-1"></i> Return Home
            </a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
