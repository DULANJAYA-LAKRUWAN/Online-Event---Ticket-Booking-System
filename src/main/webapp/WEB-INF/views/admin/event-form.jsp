<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${not empty event}" />
<c:set var="pageTitle" value="${isEdit ? 'Edit Event' : 'Create New Event'} | Admin Console" scope="request" />
<c:set var="pageActive" value="admin-events" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-4" style="max-width: 900px;">
    <!-- Breadcrumb & Header -->
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1 small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Home</a></li>
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/events" class="text-decoration-none">Events</a></li>
                    <li class="breadcrumb-item active" aria-current="page">${isEdit ? 'Edit' : 'Create'}</li>
                </ol>
            </nav>
            <h2 class="fw-bold mb-0 text-slate-900">${isEdit ? 'Edit Event Details' : 'Create New Event Listing'}</h2>
        </div>
        <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Back to Events
        </a>
    </div>

    <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

    <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-5">
        <div class="card-header bg-dark text-white py-3">
            <h5 class="mb-0 fw-semibold">
                <i class="bi ${isEdit ? 'bi-pencil-square' : 'bi-calendar-plus'} me-2 text-primary"></i>
                ${isEdit ? 'Update Event Specification' : 'New Event Specification'}
            </h5>
        </div>
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/admin/events/form" method="post"
                  enctype="multipart/form-data" class="needs-validation" novalidate id="eventForm">

                <c:if test="${isEdit}">
                    <input type="hidden" name="id" value="${event.id}">
                </c:if>

                <div class="row g-3">
                    <!-- Title -->
                    <div class="col-md-8">
                        <label for="title" class="form-label">Event Title <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="title" name="title"
                               value="<c:out value='${event != null ? event.title : param.title}' />"
                               placeholder="e.g. Grand Symphony Orchestra &amp; Acoustic Live" required minlength="3" maxlength="150">
                        <div class="invalid-feedback">Event title is required (3 to 150 characters).</div>
                    </div>

                    <!-- Category -->
                    <div class="col-md-4">
                        <label for="categoryId" class="form-label">Category <span class="text-danger">*</span></label>
                        <select class="form-select" id="categoryId" name="categoryId" required>
                            <option value="">-- Choose Category --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}" ${event != null && event.category.id == cat.id ? 'selected' : ''}>
                                    ${cat.name}
                                </option>
                            </c:forEach>
                        </select>
                        <div class="invalid-feedback">Please select an event category.</div>
                    </div>

                    <!-- Venue -->
                    <div class="col-md-6">
                        <label for="venue" class="form-label">Venue Name <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="bi bi-building"></i></span>
                            <input type="text" class="form-control" id="venue" name="venue"
                                   value="<c:out value='${event != null ? event.venue : param.venue}' />"
                                   placeholder="e.g. Metro Convention Center" required>
                            <div class="invalid-feedback">Venue name is required.</div>
                        </div>
                    </div>

                    <!-- Location / City -->
                    <div class="col-md-6">
                        <label for="location" class="form-label">City / Location <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="bi bi-geo-alt"></i></span>
                            <input type="text" class="form-control" id="location" name="location"
                                   value="<c:out value='${event != null ? event.location : param.location}' />"
                                   placeholder="e.g. New York, NY" required>
                            <div class="invalid-feedback">City or location is required.</div>
                        </div>
                    </div>

                    <!-- Date -->
                    <div class="col-md-6">
                        <label for="eventDate" class="form-label">Event Date <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="bi bi-calendar"></i></span>
                            <input type="date" class="form-control" id="eventDate" name="eventDate"
                                   value="${event != null ? event.eventDate : param.eventDate}" required>
                            <div class="invalid-feedback">Valid event date is required.</div>
                        </div>
                    </div>

                    <!-- Time -->
                    <div class="col-md-6">
                        <label for="eventTime" class="form-label">Start Time <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="bi bi-clock"></i></span>
                            <input type="time" class="form-control" id="eventTime" name="eventTime"
                                   value="${event != null ? event.eventTime : param.eventTime}" required>
                            <div class="invalid-feedback">Event start time is required.</div>
                        </div>
                    </div>

                    <!-- Banner Image Upload -->
                    <div class="col-md-12">
                        <label for="bannerImage" class="form-label">Event Banner Image</label>
                        <div class="input-group">
                            <input type="file" class="form-control" id="bannerImage" name="bannerImage" accept=".jpg,.jpeg,.png,.webp">
                        </div>
                        <small class="text-muted d-block mt-1">Accepted formats: JPG, PNG, WebP (Max 5 MB). File is stored safely on server.</small>

                        <c:if test="${isEdit && not empty event.bannerImage}">
                            <div class="mt-2 p-2 border rounded-3 bg-light d-flex align-items-center">
                                <img src="${pageContext.request.contextPath}/${event.bannerImage}" alt="Current Banner"
                                     class="rounded me-3 shadow-sm object-fit-cover" style="width: 100px; height: 60px;">
                                <div>
                                    <span class="small fw-semibold d-block text-dark">Current Banner</span>
                                    <span class="small text-muted">${event.bannerImage}</span>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <!-- Description -->
                    <div class="col-12">
                        <label for="description" class="form-label">Event Description <span class="text-danger">*</span></label>
                        <textarea class="form-control" id="description" name="description" rows="5"
                                  placeholder="Describe the event, lineup, highlights, dress code, and guidelines..." required><c:out value='${event != null ? event.description : param.description}' /></textarea>
                        <div class="invalid-feedback">Event description is required.</div>
                    </div>

                    <!-- Status -->
                    <div class="col-md-6">
                        <label for="status" class="form-label">Publication Status</label>
                        <select class="form-select" id="status" name="status">
                            <option value="DRAFT" ${event != null && event.status == 'DRAFT' ? 'selected' : ''}>
                                Draft (Hidden from public browsing)
                            </option>
                            <option value="PUBLISHED" ${event != null && event.status == 'PUBLISHED' ? 'selected' : ''}>
                                Published (Available for booking)
                            </option>
                            <option value="CANCELLED" ${event != null && event.status == 'CANCELLED' ? 'selected' : ''}>
                                Cancelled
                            </option>
                        </select>
                    </div>

                    <!-- Featured Flag -->
                    <div class="col-md-6 d-flex align-items-end">
                        <div class="form-check p-3 border rounded-3 w-100 bg-light">
                            <input class="form-check-input ms-0 me-2" type="checkbox" id="featured" name="featured" value="true"
                                   ${event != null && event.featured ? 'checked' : ''}>
                            <label class="form-check-label fw-semibold text-dark" for="featured">
                                <i class="bi bi-star-fill text-warning me-1"></i> Highlight as Featured Event on Homepage
                            </label>
                        </div>
                    </div>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                    <a href="${pageContext.request.contextPath}/admin/events" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary-event px-4">
                        <i class="bi bi-check2-circle me-1"></i> ${isEdit ? 'Save Changes' : 'Create Event'}
                    </button>
                </div>
            </form>
        </div>
    </div>
</main>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const form = document.getElementById('eventForm');
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
