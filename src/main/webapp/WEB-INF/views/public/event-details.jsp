<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${event.title} | EventCart" scope="request" />
<c:set var="pageActive" value="events" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="py-4">
    <div class="container">
        <!-- Breadcrumb -->
        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb small">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Home</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/events" class="text-decoration-none">Events</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/events?category=${event.category.id}" class="text-decoration-none">${event.category.name}</a></li>
                <li class="breadcrumb-item active text-truncate" style="max-width: 250px;" aria-current="page">${event.title}</li>
            </ol>
        </nav>

        <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

        <div class="row g-4">
            <!-- Left Main Column: Event Media, Title & Description -->
            <div class="col-lg-8">
                <!-- Large Event Banner Image -->
                <div class="rounded-4 overflow-hidden shadow-sm mb-4 bg-dark position-relative" style="max-height: 420px;">
                    <c:choose>
                        <c:when test="${not empty event.bannerImage}">
                            <img src="${pageContext.request.contextPath}/${event.bannerImage}" alt="${event.title}"
                                 class="w-100 object-fit-cover" style="max-height: 420px;">
                        </c:when>
                        <c:otherwise>
                            <svg class="w-100" height="340" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img">
                                <defs>
                                    <linearGradient id="detailGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                                        <stop offset="0%" style="stop-color:#0f172a;stop-opacity:1" />
                                        <stop offset="100%" style="stop-color:#312e81;stop-opacity:1" />
                                    </linearGradient>
                                </defs>
                                <rect width="100%" height="100%" fill="url(#detailGrad)"/>
                                <text x="50%" y="45%" fill="#e0e7ff" font-family="Inter, sans-serif" font-weight="800" font-size="28" text-anchor="middle">
                                    <c:out value="${event.title}" />
                                </text>
                                <text x="50%" y="60%" fill="#a5b4fc" font-family="Inter, sans-serif" font-size="16" text-anchor="middle">
                                    <c:out value="${event.category.name}" /> &bull; <c:out value="${event.venue}" />
                                </text>
                            </svg>
                        </c:otherwise>
                    </c:choose>
                    <span class="position-absolute top-0 start-0 m-3 badge bg-dark text-white px-3 py-2 border border-secondary">
                        <i class="bi ${event.category.iconClass != null ? event.category.iconClass : 'bi-tag'} me-1 text-primary"></i>
                        ${event.category.name}
                    </span>
                    <c:if test="${event.featured}">
                        <span class="position-absolute top-0 end-0 m-3 badge bg-primary text-white px-3 py-2">
                            <i class="bi bi-star-fill text-warning me-1"></i> Featured Experience
                        </span>
                    </c:if>
                </div>

                <!-- Event Title & Basic Metadata -->
                <div class="mb-4">
                    <h1 class="fw-bold mb-3 text-slate-900">${event.title}</h1>
                    <div class="d-flex flex-wrap gap-4 text-muted small pb-3 border-bottom">
                        <div>
                            <i class="bi bi-calendar3 me-1 text-primary fs-6"></i>
                            <strong>Date:</strong> ${event.eventDate}
                        </div>
                        <div>
                            <i class="bi bi-clock me-1 text-primary fs-6"></i>
                            <strong>Time:</strong> ${event.eventTime}
                        </div>
                        <div>
                            <i class="bi bi-geo-alt me-1 text-danger fs-6"></i>
                            <strong>Venue:</strong> ${event.venue}, ${event.location}
                        </div>
                    </div>
                </div>

                <!-- Full Event Description -->
                <div class="card border-0 shadow-sm rounded-4 p-4 mb-4 bg-white">
                    <h4 class="fw-bold mb-3 text-slate-900">About This Event</h4>
                    <div class="text-secondary lh-lg" style="white-space: pre-line;">
                        <c:out value="${event.description}" />
                    </div>
                </div>

                <!-- Available Ticket Tiers (Ready for Cart Integration in Next Phase) -->
                <div class="card border-0 shadow-sm rounded-4 p-4 mb-4 bg-white" id="tickets-section">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="fw-bold mb-0 text-slate-900">Select Tickets</h4>
                        <span class="badge bg-light text-secondary border">Real-Time Inventory</span>
                    </div>

                    <c:choose>
                        <c:when test="${not empty tickets}">
                            <div class="d-flex flex-column gap-3">
                                <c:forEach var="tk" items="${tickets}">
                                    <div class="p-3 border rounded-3 d-flex flex-wrap justify-content-between align-items-center ${tk.availableQuantity <= 0 ? 'bg-light opacity-75' : 'bg-white'}">
                                        <div class="me-3 mb-2 mb-md-0" style="max-width: 420px;">
                                            <div class="d-flex align-items-center">
                                                <h5 class="fw-bold mb-1 text-dark">${tk.name}</h5>
                                                <c:choose>
                                                    <c:when test="${tk.availableQuantity > 0}">
                                                        <span class="badge bg-success-subtle text-success border border-success-subtle ms-2 small">Available</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger-subtle text-danger border border-danger-subtle ms-2 small">Sold Out</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <p class="text-muted small mb-0"><c:out value="${tk.description != null ? tk.description : 'Standard access admission.'}" /></p>
                                        </div>

                                        <div class="text-md-end d-flex align-items-center gap-3">
                                            <div>
                                                <span class="fw-bold fs-4 text-primary">$${tk.price}</span>
                                                <span class="small text-muted d-block">${tk.availableQuantity} seats left</span>
                                            </div>

                                            <c:choose>
                                                <c:when test="${tk.availableQuantity > 0}">
                                                    <!-- Quantity Selector and AJAX Add to Cart -->
                                                    <div class="d-flex align-items-center gap-2">
                                                        <div class="input-group input-group-sm" style="width: 110px;">
                                                             <button class="btn btn-outline-secondary" type="button" aria-label="Decrease quantity" onclick="decrementQty(${tk.id})">&minus;</button>
                                                             <input type="number" class="form-control text-center px-1" id="qty-${tk.id}" value="1" min="1" max="${tk.availableQuantity > 10 ? 10 : tk.availableQuantity}" aria-label="Quantity for ${tk.name}" readonly>
                                                             <button class="btn btn-outline-secondary" type="button" aria-label="Increase quantity" onclick="incrementQty(${tk.id}, ${tk.availableQuantity > 10 ? 10 : tk.availableQuantity})">&plus;</button>
                                                        </div>
                                                        <button type="button" class="btn btn-primary-event btn-sm text-nowrap" id="btn-add-${tk.id}"
                                                                onclick="handleAddToCart(${tk.id}, this)">
                                                            <i class="bi bi-cart-plus me-1"></i> Add to Cart
                                                        </button>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" class="btn btn-secondary btn-sm" disabled>Sold Out</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:choose>
                        <c:otherwise>
                            <div class="alert alert-info border-0 shadow-sm mb-0">
                                <i class="bi bi-info-circle me-2"></i> Ticket sales for this event have not opened yet. Please check back soon.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Right Sidebar: Quick Summary & Venue Info -->
            <div class="col-lg-4">
                <div class="card border-0 shadow-sm rounded-4 p-4 bg-white sticky-top" style="top: 90px; z-index: 10;">
                    <h5 class="fw-bold mb-3 text-slate-900">Event Overview</h5>

                    <ul class="list-unstyled mb-4 small">
                        <li class="py-2 border-bottom d-flex justify-content-between">
                            <span class="text-muted"><i class="bi bi-calendar-event me-2 text-primary"></i>Date</span>
                            <span class="fw-semibold text-dark">${event.eventDate}</span>
                        </li>
                        <li class="py-2 border-bottom d-flex justify-content-between">
                            <span class="text-muted"><i class="bi bi-clock me-2 text-primary"></i>Start Time</span>
                            <span class="fw-semibold text-dark">${event.eventTime}</span>
                        </li>
                        <li class="py-2 border-bottom d-flex justify-content-between">
                            <span class="text-muted"><i class="bi bi-building me-2 text-primary"></i>Venue</span>
                            <span class="fw-semibold text-dark text-truncate" style="max-width: 170px;" title="${event.venue}">${event.venue}</span>
                        </li>
                        <li class="py-2 border-bottom d-flex justify-content-between">
                            <span class="text-muted"><i class="bi bi-geo-alt me-2 text-primary"></i>Location</span>
                            <span class="fw-semibold text-dark">${event.location}</span>
                        </li>
                        <li class="py-2 d-flex justify-content-between">
                            <span class="text-muted"><i class="bi bi-currency-dollar me-2 text-primary"></i>Price Starts</span>
                            <span class="fw-bold text-success fs-6">$${event.startingPrice}</span>
                        </li>
                    </ul>

                    <a href="#tickets-section" class="btn btn-primary-event w-100 py-2 mb-3">
                        <i class="bi bi-ticket-detailed me-1"></i> Book Tickets Now
                    </a>

                    <!-- Guarantee Badge -->
                    <div class="p-3 bg-light rounded-3 border small">
                        <div class="fw-bold text-dark mb-1">
                            <i class="bi bi-shield-check me-1 text-success"></i> 100% Authentic Guarantee
                        </div>
                        <p class="text-muted mb-0">Every ticket is digitally authenticated with cryptographic QR reference codes.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<script>
    function decrementQty(ticketId) {
        const input = document.getElementById('qty-' + ticketId);
        if (!input) return;
        let val = parseInt(input.value, 10);
        if (isNaN(val) || val <= 1) return;
        input.value = val - 1;
    }

    function incrementQty(ticketId, maxQty) {
        const input = document.getElementById('qty-' + ticketId);
        if (!input) return;
        let val = parseInt(input.value, 10);
        if (isNaN(val)) val = 1;
        if (val < maxQty) {
            input.value = val + 1;
        }
    }

    async function handleAddToCart(ticketId, buttonEl) {
        const input = document.getElementById('qty-' + ticketId);
        const qty = input ? parseInt(input.value, 10) : 1;
        if (isNaN(qty) || qty < 1) {
            EventCart.showNotification('Please select a valid ticket quantity.', 'warning');
            return;
        }

        const originalHtml = buttonEl.innerHTML;
        buttonEl.disabled = true;
        buttonEl.innerHTML = '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> Adding...';

        try {
            const formData = new URLSearchParams();
            formData.append('ticketTypeId', ticketId);
            formData.append('quantity', qty);

            const res = await EventCart.request('${pageContext.request.contextPath}/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: formData.toString()
            });

            if (res && res.success) {
                EventCart.updateCartBadge(res.data.cartCount);
                EventCart.showNotification(
                    'Added ' + qty + ' ticket' + (qty > 1 ? 's' : '') + ' to your cart! <a href="${pageContext.request.contextPath}/cart" class="text-white fw-bold text-decoration-underline ms-1">View Cart</a>',
                    'success'
                );
            } else {
                EventCart.showNotification(res.message || 'Failed to add ticket to cart.', 'error');
            }
        } catch (err) {
            EventCart.showNotification(err.message || 'Error adding ticket to cart.', 'error');
        } finally {
            buttonEl.disabled = false;
            buttonEl.innerHTML = originalHtml;
        }
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
