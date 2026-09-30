<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Categories | Admin Console" scope="request" />
<c:set var="pageActive" value="admin-categories" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="container my-4">
    <!-- Breadcrumb & Title -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-1 small">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home" class="text-decoration-none">Home</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Admin Console</li>
                    <li class="breadcrumb-item active" aria-current="page">Categories</li>
                </ol>
            </nav>
            <h2 class="fw-bold mb-0 text-slate-900">Event Categories</h2>
        </div>
        <div class="mt-2 mt-sm-0">
            <button type="button" class="btn btn-primary-event" data-bs-toggle="modal" data-bs-target="#createCategoryModal">
                <i class="bi bi-folder-plus me-1"></i> Add Category
            </button>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/alerts.jspf" %>

    <!-- Categories Card -->
    <div class="card border-0 shadow-sm rounded-4 overflow-hidden mb-5">
        <div class="card-header bg-white py-3 border-0 d-flex justify-content-between align-items-center">
            <span class="fw-semibold text-secondary">
                <i class="bi bi-grid-fill me-1 text-primary"></i> Total Categories: <span class="badge bg-light text-dark border">${categories != null ? categories.size() : 0}</span>
            </span>
            <small class="text-muted"><i class="bi bi-info-circle me-1"></i> Click status badge to toggle state asynchronously via AJAX</small>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4" style="width: 70px;">ID</th>
                        <th>Category Name</th>
                        <th>Slug</th>
                        <th>Description</th>
                        <th class="text-center" style="width: 140px;">Status</th>
                        <th class="text-end pe-4" style="width: 160px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty categories}">
                            <c:forEach var="cat" items="${categories}">
                                <tr id="category-row-${cat.id}">
                                    <td class="ps-4 fw-semibold text-muted">#${cat.id}</td>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <span class="p-2 rounded-3 bg-light text-primary me-2 border">
                                                <i class="bi ${cat.iconClass != null ? cat.iconClass : 'bi-tag'} fs-6"></i>
                                            </span>
                                            <div>
                                                <span class="fw-bold text-dark d-block">${cat.name}</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <code class="text-secondary small bg-light px-2 py-1 rounded">${cat.slug}</code>
                                    </td>
                                    <td class="text-muted small">
                                        <c:out value="${cat.description != null ? cat.description : '—'}" />
                                    </td>
                                    <td class="text-center">
                                        <button type="button" class="btn btn-sm border-0 p-0 status-toggle-btn"
                                                onclick="toggleCategoryStatus(${cat.id})"
                                                title="Click to toggle status via AJAX">
                                            <span id="cat-badge-${cat.id}" class="badge ${cat.status == 'ACTIVE' ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-secondary-subtle text-secondary border border-secondary-subtle'} rounded-pill px-3 py-2">
                                                <i class="bi ${cat.status == 'ACTIVE' ? 'bi-check-circle-fill' : 'bi-pause-circle-fill'} me-1"></i>
                                                <span id="cat-status-text-${cat.id}">${cat.status.displayName}</span>
                                            </span>
                                        </button>
                                    </td>
                                    <td class="text-end pe-4">
                                        <button type="button" class="btn btn-sm btn-outline-secondary me-1"
                                                onclick="openEditCategoryModal(${cat.id}, '${cat.name}', '${cat.iconClass}', '${cat.status}', '<c:out value="${cat.description}" />')"
                                                title="Edit Category">
                                            <i class="bi bi-pencil"></i>
                                        </button>
                                        <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="d-inline"
                                              onsubmit="return confirm('Are you sure you want to deactivate or remove category &quot;${cat.name}&quot;?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${cat.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete or Deactivate">
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
                                    <i class="bi bi-inbox fs-2 d-block mb-2 text-secondary"></i>
                                    No event categories created yet. Click <strong>Add Category</strong> to begin.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</main>

<!-- Create Category Modal -->
<div class="modal fade" id="createCategoryModal" tabindex="-1" aria-labelledby="createCategoryModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-dark text-white">
                <h5 class="modal-title fw-bold" id="createCategoryModalLabel">
                    <i class="bi bi-plus-circle me-1 text-primary"></i> Create Category
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="needs-validation" novalidate>
                <input type="hidden" name="action" value="create">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="createName" class="form-label">Category Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="createName" name="name" placeholder="e.g. Music &amp; Concerts" required minlength="2" maxlength="80">
                        <div class="invalid-feedback">Category name is required (2-80 characters).</div>
                    </div>
                    <div class="mb-3">
                        <label for="createIcon" class="form-label">Bootstrap Icon Class</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="bi bi-palette"></i></span>
                            <input type="text" class="form-control" id="createIcon" name="iconClass" value="bi-tag" placeholder="e.g. bi-music-note-beamed">
                        </div>
                        <small class="text-muted">Use any Bootstrap 5 icon class (e.g., <code>bi-cpu</code>, <code>bi-trophy</code>, <code>bi-palette</code>).</small>
                    </div>
                    <div class="mb-3">
                        <label for="createStatus" class="form-label">Status</label>
                        <select class="form-select" id="createStatus" name="status">
                            <option value="ACTIVE" selected>Active (Visible to public)</option>
                            <option value="INACTIVE">Inactive (Hidden from public)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="createDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="createDescription" name="description" rows="3" placeholder="Brief summary of events included in this category..."></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary-event">Save Category</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Edit Category Modal -->
<div class="modal fade" id="editCategoryModal" tabindex="-1" aria-labelledby="editCategoryModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-dark text-white">
                <h5 class="modal-title fw-bold" id="editCategoryModalLabel">
                    <i class="bi bi-pencil-square me-1 text-primary"></i> Edit Category
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="needs-validation" novalidate id="editCategoryForm">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" id="editId">
                <div class="modal-body p-4">
                    <div class="mb-3">
                        <label for="editName" class="form-label">Category Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="editName" name="name" required minlength="2" maxlength="80">
                        <div class="invalid-feedback">Category name is required (2-80 characters).</div>
                    </div>
                    <div class="mb-3">
                        <label for="editIcon" class="form-label">Bootstrap Icon Class</label>
                        <input type="text" class="form-control" id="editIcon" name="iconClass">
                    </div>
                    <div class="mb-3">
                        <label for="editStatus" class="form-label">Status</label>
                        <select class="form-select" id="editStatus" name="status">
                            <option value="ACTIVE">Active (Visible to public)</option>
                            <option value="INACTIVE">Inactive (Hidden from public)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="editDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="editDescription" name="description" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary-event">Update Category</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- AJAX Status Toggle & Edit Handler Script -->
<script>
    function openEditCategoryModal(id, name, iconClass, status, description) {
        document.getElementById('editId').value = id;
        document.getElementById('editName').value = name;
        document.getElementById('editIcon').value = iconClass || 'bi-tag';
        document.getElementById('editStatus').value = status;
        document.getElementById('editDescription').value = description || '';

        const modal = new bootstrap.Modal(document.getElementById('editCategoryModal'));
        modal.show();
    }

    async function toggleCategoryStatus(categoryId) {
        try {
            const formData = new URLSearchParams();
            formData.append('action', 'toggle');
            formData.append('id', categoryId);

            const result = await EventCart.request('${pageContext.request.contextPath}/admin/categories', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: formData.toString()
            });

            if (result && result.success && result.data) {
                const badge = document.getElementById('cat-badge-' + categoryId);
                const text = document.getElementById('cat-status-text-' + categoryId);
                const isNowActive = result.data.status === 'ACTIVE';

                badge.className = isNowActive
                    ? 'badge bg-success-subtle text-success border border-success-subtle rounded-pill px-3 py-2'
                    : 'badge bg-secondary-subtle text-secondary border border-secondary-subtle rounded-pill px-3 py-2';

                badge.querySelector('i').className = isNowActive
                    ? 'bi bi-check-circle-fill me-1'
                    : 'bi bi-pause-circle-fill me-1';

                text.textContent = result.data.statusDisplay;
                EventCart.showNotification(result.message, 'success');
            }
        } catch (error) {
            EventCart.showNotification('Failed to toggle category status: ' + error.message, 'error');
        }
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
