<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Tickets: ${event.title} | Admin Console" scope="request" />
<c:set var="pageActive" value="admin-events" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-4">
    <!-- Breadcrumb & Header -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1 small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Home</a></li>
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/events" class="text-decoration-none">Events</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Tickets</li>
                </ol>
            </nav>
            <h2 class="fw-bold mb-0 text-slate-900">Manage Ticket Tiers</h2>
            <div class="text-muted small mt-1">
                Event: <strong class="text-dark"><c:out value="${event.title}" /></strong> &bull;
                <span><i class="bi bi-calendar3 me-1"></i>${event.eventDate} at ${event.eventTime}</span> &bull;
                <span><i class="bi bi-geo-alt me-1"></i>${event.venue}, ${event.location}</span>
            </div>
        </div>
        <div class="mt-2 mt-sm-0 d-flex gap-2">
            <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left me-1"></i> Events List
            </a>
            <button type="button" class="btn btn-primary-event btn-sm" data-bs-toggle="modal" data-bs-target="#createTicketModal">
                <i class="bi bi-plus-circle me-1"></i> Add Ticket Tier
            </button>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

    <!-- Ticket Tiers Table Card -->
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-5">
        <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
            <span class="fw-semibold text-secondary">
                <i class="bi bi-ticket-detailed-fill me-1 text-primary"></i> Configured Tiers: <span class="badge bg-light text-dark border">${tickets != null ? tickets.size() : 0}</span>
            </span>
            <span class="badge bg-info-subtle text-info border border-info-subtle">
                <i class="bi bi-info-circle me-1"></i> Inventory automatically synchronized on checkout
            </span>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Tier Name</th>
                        <th>Price</th>
                        <th class="text-center">Total Seats</th>
                        <th class="text-center">Available Seats</th>
                        <th class="text-center">Status</th>
                        <th class="text-end pe-4" style="width: 150px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty tickets}">
                            <c:forEach var="tk" items="${tickets}">
                                <tr>
                                    <td class="ps-4">
                                        <div class="fw-bold text-dark">${tk.name}</div>
                                        <small class="text-muted"><c:out value="${tk.description != null ? tk.description : '—'}" /></small>
                                    </td>
                                    <td>
                                        <span class="fw-bold text-primary fs-6">$${tk.price}</span>
                                    </td>
                                    <td class="text-center fw-semibold text-dark">${tk.totalQuantity}</td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${tk.availableQuantity > 0}">
                                                <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill px-3 py-1">
                                                    ${tk.availableQuantity} in stock
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger-subtle text-danger border border-danger-subtle rounded-pill px-3 py-1">
                                                    Sold Out
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge ${tk.status == 'ACTIVE' ? 'bg-success text-light' : 'bg-secondary text-light'} rounded-pill">
                                            ${tk.status}
                                        </span>
                                    </td>
                                    <td class="text-end pe-4">
                                        <!-- Edit button -->
                                        <button type="button" class="btn btn-sm btn-outline-secondary me-1"
                                                onclick="openEditTicketModal(${tk.id}, '${tk.name}', '${tk.price}', ${tk.totalQuantity}, ${tk.availableQuantity}, '${tk.status}', '<c:out value="${tk.description}" />')"
                                                title="Edit Tier">
                                            <i class="bi bi-pencil"></i>
                                        </button>

                                        <!-- Toggle Status -->
                                        <form action="${pageContext.request.contextPath}/admin/events/tickets" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="eventId" value="${event.id}">
                                            <input type="hidden" name="id" value="${tk.id}">
                                            <button type="submit" class="btn btn-sm ${tk.status == 'ACTIVE' ? 'btn-outline-warning' : 'btn-outline-success'} me-1"
                                                    title="${tk.status == 'ACTIVE' ? 'Deactivate Tier' : 'Activate Tier'}">
                                                <i class="bi ${tk.status == 'ACTIVE' ? 'bi-pause-circle' : 'bi-play-circle'}"></i>
                                            </button>
                                        </form>

                                        <!-- Delete -->
                                        <form action="${pageContext.request.contextPath}/admin/events/tickets" method="post" class="d-inline"
                                              onsubmit="return confirm('Deactivate ticket tier &quot;${tk.name}&quot;?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="eventId" value="${event.id}">
                                            <input type="hidden" name="id" value="${tk.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" class="text-center py-5 text-muted">
                                    <i class="bi bi-ticket fs-2 d-block mb-2 text-secondary"></i>
                                    No ticket types configured for this event. Click <strong>Add Ticket Tier</strong> to begin ticket sales.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</main>

<!-- Create Ticket Modal -->
<div class="modal fade" id="createTicketModal" tabindex="-1" aria-labelledby="createTicketModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-dark text-white">
                <h5 class="modal-title fw-bold" id="createTicketModalLabel">
                    <i class="bi bi-plus-circle me-1 text-primary"></i> Add Ticket Tier
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/events/tickets" method="post" class="needs-validation" novalidate>
                <input type="hidden" name="action" value="create">
                <input type="hidden" name="eventId" value="${event.id}">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="createName" class="form-label">Tier Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="createName" name="name" placeholder="e.g. VIP Experience, General Admission" required minlength="2" maxlength="80">
                        <div class="invalid-feedback">Ticket tier name is required (2-80 characters).</div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-6">
                            <label for="createPrice" class="form-label">Price ($) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="createPrice" name="price" placeholder="49.00" required>
                            <div class="invalid-feedback">Price must be zero or positive.</div>
                        </div>
                        <div class="col-6">
                            <label for="createQuantity" class="form-label">Total Quantity <span class="text-danger">*</span></label>
                            <input type="number" min="1" class="form-control" id="createQuantity" name="totalQuantity" placeholder="100" required>
                            <div class="invalid-feedback">Quantity must be at least 1.</div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="createStatus" class="form-label">Status</label>
                        <select class="form-select" id="createStatus" name="status">
                            <option value="ACTIVE" selected>Active (Available for booking)</option>
                            <option value="INACTIVE">Inactive (Disabled)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="createDescription" class="form-label">Perks &amp; Seating Description</label>
                        <textarea class="form-control" id="createDescription" name="description" rows="3" placeholder="e.g. Front-row seating, free refreshments, priority entry..."></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary-event">Save Ticket Tier</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Ticket Modal -->
<div class="modal fade" id="editTicketModal" tabindex="-1" aria-labelledby="editTicketModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-dark text-white">
                <h5 class="modal-title fw-bold" id="editTicketModalLabel">
                    <i class="bi bi-pencil-square me-1 text-primary"></i> Edit Ticket Tier
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/events/tickets" method="post" class="needs-validation" novalidate id="editTicketForm">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="eventId" value="${event.id}">
                <input type="hidden" name="id" id="editTicketId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="editTicketName" class="form-label">Tier Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="editTicketName" name="name" required minlength="2" maxlength="80">
                        <div class="invalid-feedback">Tier name is required.</div>
                    </div>
                    <div class="row g-2 mb-3">
                        <div class="col-4">
                            <label for="editTicketPrice" class="form-label">Price ($) <span class="text-danger">*</span></label>
                            <input type="number" step="0.01" min="0" class="form-control" id="editTicketPrice" name="price" required>
                        </div>
                        <div class="col-4">
                            <label for="editTicketTotalQty" class="form-label">Total Seats <span class="text-danger">*</span></label>
                            <input type="number" min="1" class="form-control" id="editTicketTotalQty" name="totalQuantity" required>
                        </div>
                        <div class="col-4">
                            <label for="editTicketAvailQty" class="form-label">Available <span class="text-danger">*</span></label>
                            <input type="number" min="0" class="form-control" id="editTicketAvailQty" name="availableQuantity" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="editTicketStatus" class="form-label">Status</label>
                        <select class="form-select" id="editTicketStatus" name="status">
                            <option value="ACTIVE">Active (Available for booking)</option>
                            <option value="INACTIVE">Inactive (Disabled)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="editTicketDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="editTicketDescription" name="description" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary-event">Update Tier</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function openEditTicketModal(id, name, price, totalQty, availQty, status, description) {
        document.getElementById('editTicketId').value = id;
        document.getElementById('editTicketName').value = name;
        document.getElementById('editTicketPrice').value = price;
        document.getElementById('editTicketTotalQty').value = totalQty;
        document.getElementById('editTicketAvailQty').value = availQty;
        document.getElementById('editTicketStatus').value = status;
        document.getElementById('editTicketDescription').value = description || '';

        const modal = new bootstrap.Modal(document.getElementById('editTicketModal'));
        modal.show();
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
