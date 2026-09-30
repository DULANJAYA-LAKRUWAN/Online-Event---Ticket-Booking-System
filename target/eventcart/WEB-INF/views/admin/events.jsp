<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Events | Admin Console" scope="request" />
<c:set var="pageActive" value="admin-events" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container-fluid px-lg-5 my-4">
    <!-- Breadcrumb & Actions Bar -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1 small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Home</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Admin Console</li>
                    <li class="breadcrumb-item active" aria-current="page">Events</li>
                </ol>
            </nav>
            <h2 class="fw-bold mb-0 text-slate-900">Event Listings Management</h2>
        </div>
        <div class="mt-2 mt-sm-0 d-flex gap-2">
            <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-outline-secondary">
                <i class="bi bi-tags me-1"></i> Manage Categories
            </a>
            <a href="${pageContext.request.contextPath}/admin/events/form" class="btn btn-primary-event">
                <i class="bi bi-calendar-plus me-1"></i> Create New Event
            </a>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

    <!-- Event List Card -->
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-5">
        <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
            <span class="fw-semibold text-secondary">
                <i class="bi bi-calendar-event-fill me-1 text-primary"></i> Total Registered Events: <span class="badge bg-light text-dark border">${events != null ? events.size() : 0}</span>
            </span>
            <small class="text-muted"><i class="bi bi-shield-check me-1"></i> Changes take effect immediately across discovery</small>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4" style="width: 60px;">ID</th>
                        <th style="width: 100px;">Banner</th>
                        <th>Event Details</th>
                        <th>Category</th>
                        <th>Date &amp; Venue</th>
                        <th class="text-center">Status</th>
                        <th class="text-center">Featured</th>
                        <th class="text-end pe-4" style="width: 220px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty events}">
                            <c:forEach var="ev" items="${events}">
                                <tr>
                                    <td class="ps-4 fw-bold text-muted">#${ev.id}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty ev.bannerImage}">
                                                <img src="${pageContext.request.contextPath}/${ev.bannerImage}" alt="${ev.title}"
                                                     class="rounded-3 object-fit-cover shadow-sm" style="width: 80px; height: 50px;">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="rounded-3 bg-light text-secondary d-flex align-items-center justify-content-center border"
                                                     style="width: 80px; height: 50px;">
                                                    <i class="bi bi-image fs-5 text-muted"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/event?slug=${ev.slug}" target="_blank"
                                           class="fw-bold text-dark text-decoration-none d-block text-truncate" style="max-width: 280px;" title="${ev.title}">
                                            ${ev.title}
                                        </a>
                                        <small class="text-muted text-truncate d-block" style="max-width: 280px;">
                                            Slug: <code class="text-secondary">${ev.slug}</code>
                                        </small>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-dark border">
                                            <i class="bi ${ev.category.iconClass != null ? ev.category.iconClass : 'bi-tag'} me-1 text-primary"></i>
                                            ${ev.category.name}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="small fw-semibold text-dark">
                                            <i class="bi bi-calendar3 me-1 text-primary"></i> ${ev.eventDate} &bull; ${ev.eventTime}
                                        </div>
                                        <div class="small text-muted text-truncate" style="max-width: 200px;">
                                            <i class="bi bi-geo-alt me-1 text-danger"></i> ${ev.venue}, ${ev.location}
                                        </div>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${ev.status == 'PUBLISHED'}">
                                                <span class="badge bg-success-subtle text-success border border-success-subtle rounded-pill px-3 py-1">
                                                    <i class="bi bi-check-circle-fill me-1"></i> Published
                                                </span>
                                            </c:when>
                                            <c:when test="${ev.status == 'DRAFT'}">
                                                <span class="badge bg-warning-subtle text-warning border border-warning-subtle rounded-pill px-3 py-1">
                                                    <i class="bi bi-pencil-fill me-1"></i> Draft
                                                </span>
                                            </c:when>
                                            <c:when test="${ev.status == 'CANCELLED'}">
                                                <span class="badge bg-danger-subtle text-danger border border-danger-subtle rounded-pill px-3 py-1">
                                                    <i class="bi bi-x-circle-fill me-1"></i> Cancelled
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary rounded-pill px-3 py-1">${ev.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <c:if test="${ev.featured}">
                                            <span class="badge bg-primary text-light rounded-pill"><i class="bi bi-star-fill me-1"></i> Yes</span>
                                        </c:if>
                                        <c:if test="${!ev.featured}">
                                            <span class="text-muted small">—</span>
                                        </c:if>
                                    </td>
                                    <td class="text-end pe-4">
                                        <!-- Manage Tickets -->
                                        <a href="${pageContext.request.contextPath}/admin/events/tickets?eventId=${ev.id}"
                                           class="btn btn-sm btn-outline-info me-1" title="Manage Ticket Tiers">
                                            <i class="bi bi-ticket-perforated"></i>
                                        </a>

                                        <!-- Edit -->
                                        <a href="${pageContext.request.contextPath}/admin/events/form?id=${ev.id}"
                                           class="btn btn-sm btn-outline-secondary me-1" title="Edit Event Details">
                                            <i class="bi bi-pencil"></i>
                                        </a>

                                        <!-- Status Transition Dropdown -->
                                        <div class="btn-group">
                                            <button type="button" class="btn btn-sm btn-outline-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false" title="Change Status">
                                                <i class="bi bi-arrow-repeat"></i>
                                            </button>
                                            <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                                                <li><h6 class="dropdown-header">Update Status</h6></li>
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/admin/events" method="post">
                                                        <input type="hidden" name="action" value="status">
                                                        <input type="hidden" name="id" value="${ev.id}">
                                                        <input type="hidden" name="status" value="PUBLISHED">
                                                        <button type="submit" class="dropdown-item text-success"><i class="bi bi-check2-circle me-2"></i>Publish Event</button>
                                                    </form>
                                                </li>
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/admin/events" method="post">
                                                        <input type="hidden" name="action" value="status">
                                                        <input type="hidden" name="id" value="${ev.id}">
                                                        <input type="hidden" name="status" value="DRAFT">
                                                        <button type="submit" class="dropdown-item text-warning"><i class="bi bi-file-earmark-text me-2"></i>Revert to Draft</button>
                                                    </form>
                                                </li>
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/admin/events" method="post">
                                                        <input type="hidden" name="action" value="status">
                                                        <input type="hidden" name="id" value="${ev.id}">
                                                        <input type="hidden" name="status" value="CANCELLED">
                                                        <button type="submit" class="dropdown-item text-danger"><i class="bi bi-slash-circle me-2"></i>Cancel Event</button>
                                                    </form>
                                                </li>
                                            </ul>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" class="text-center py-5 text-muted">
                                    <i class="bi bi-calendar-x fs-2 d-block mb-2 text-secondary"></i>
                                    No events registered. Click <strong>Create New Event</strong> to get started.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
