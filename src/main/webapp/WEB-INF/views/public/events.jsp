<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Discover &amp; Explore Events | EventCart" scope="request" />
<c:set var="pageActive" value="events" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main class="py-4">
    <div class="container">
        <!-- Page Title & Hero Subheader -->
        <div class="mb-4">
            <h1 class="fw-bold mb-1 text-slate-900">Explore Upcoming Events</h1>
            <p class="text-muted">Discover world-class concerts, festivals, conferences, and sports tournaments.</p>
        </div>

        <!-- Filter & Search Toolbar -->
        <div class="card border-0 shadow-sm rounded-4 p-3 mb-4 bg-white">
            <form action="${pageContext.request.contextPath}/events" method="get" class="row g-2 align-items-center">
                <div class="col-lg-4 col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-search"></i></span>
                        <input type="text" name="query" class="form-control border-start-0" placeholder="Search event title, venue, or keyword..."
                               value="<c:out value='${query}' />">
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-grid"></i></span>
                        <select name="category" class="form-select border-start-0">
                            <option value="">All Categories</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}" ${selectedCategoryId == cat.id || selectedCategorySlug == cat.slug ? 'selected' : ''}>
                                    ${cat.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6">
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0 text-muted"><i class="bi bi-geo-alt"></i></span>
                        <input type="text" name="location" class="form-control border-start-0" placeholder="City / Location"
                               value="<c:out value='${location}' />">
                    </div>
                </div>
                <div class="col-lg-2 col-md-6 d-flex gap-2">
                    <button type="submit" class="btn btn-primary-event w-100">
                        <i class="bi bi-funnel me-1"></i> Filter
                    </button>
                    <c:if test="${not empty query || not empty selectedCategoryId || not empty location}">
                        <a href="${pageContext.request.contextPath}/events" class="btn btn-outline-secondary" title="Reset Filters">
                            <i class="bi bi-x-lg"></i>
                        </a>
                    </c:if>
                </div>
            </form>
        </div>

        <!-- Category Quick Chips -->
        <div class="d-flex flex-wrap gap-2 mb-4">
            <a href="${pageContext.request.contextPath}/events"
               class="badge rounded-pill px-3 py-2 text-decoration-none ${empty selectedCategoryId && empty selectedCategorySlug ? 'text-bg-primary' : 'text-bg-light border text-dark'}">
                All Events
            </a>
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/events?category=${cat.id}"
                   class="badge rounded-pill px-3 py-2 text-decoration-none ${selectedCategoryId == cat.id || selectedCategorySlug == cat.slug ? 'text-bg-primary' : 'text-bg-light border text-dark'}">
                    <i class="bi ${cat.iconClass != null ? cat.iconClass : 'bi-tag'} me-1"></i> ${cat.name}
                </a>
            </c:forEach>
        </div>

        <!-- Event Cards Grid -->
        <c:choose>
            <c:when test="${not empty events}">
                <div class="row g-4 mb-5">
                    <c:forEach var="ev" items="${events}">
                        <div class="col-lg-4 col-md-6">
                            <div class="event-card h-100">
                                <div class="event-card-img-wrap">
                                    <c:choose>
                                        <c:when test="${not empty ev.bannerImage}">
                                            <img src="${pageContext.request.contextPath}/${ev.bannerImage}" alt="${ev.title}" class="event-card-img">
                                        </c:when>
                                        <c:otherwise>
                                            <svg class="event-card-img" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img">
                                                <defs>
                                                    <linearGradient id="grad_ev_${ev.id}" x1="0%" y1="0%" x2="100%" y2="100%">
                                                        <stop offset="0%" style="stop-color:#1e1b4b;stop-opacity:1" />
                                                        <stop offset="100%" style="stop-color:#312e81;stop-opacity:1" />
                                                    </linearGradient>
                                                </defs>
                                                <rect width="100%" height="100%" fill="url(#grad_ev_${ev.id})"/>
                                                <text x="50%" y="50%" fill="#c7d2fe" font-family="Inter, sans-serif" font-weight="700" font-size="16" text-anchor="middle">
                                                    <c:out value="${ev.category.name}" />
                                                </text>
                                            </svg>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="event-badge-category">
                                        <i class="bi ${ev.category.iconClass != null ? ev.category.iconClass : 'bi-tag'} me-1"></i>
                                        ${ev.category.name}
                                    </span>
                                    <c:set var="startingPrice" value="${ev.startingPrice}" />
                                    <c:if test="${startingPrice > 0}">
                                        <span class="event-badge-price">From $${startingPrice}</span>
                                    </c:if>
                                    <c:if test="${startingPrice == 0}">
                                        <span class="event-badge-price">Free / RSVP</span>
                                    </c:if>
                                </div>
                                <div class="event-card-body">
                                    <h5 class="event-card-title">
                                        <a href="${pageContext.request.contextPath}/event?slug=${ev.slug}" class="text-decoration-none text-dark">
                                            <c:out value="${ev.title}" />
                                        </a>
                                    </h5>
                                    <div class="event-card-meta">
                                        <i class="bi bi-calendar3 text-primary"></i>
                                        <span>${ev.eventDate} &bull; ${ev.eventTime}</span>
                                    </div>
                                    <div class="event-card-meta mb-3">
                                        <i class="bi bi-geo-alt text-danger"></i>
                                        <span class="text-truncate">${ev.venue}, ${ev.location}</span>
                                    </div>
                                    <p class="text-muted small flex-grow-1 text-truncate-3">
                                        <c:out value="${ev.description}" />
                                    </p>
                                    <div class="pt-3 border-top d-flex justify-content-between align-items-center mt-auto">
                                        <c:choose>
                                            <c:when test="${ev.totalAvailableTickets > 0}">
                                                <span class="badge bg-success-subtle text-success border border-success-subtle">
                                                    <i class="bi bi-ticket-detailed me-1"></i> ${ev.totalAvailableTickets} Seats Left
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary-subtle text-secondary border">
                                                    Check Availability
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                        <a href="${pageContext.request.contextPath}/event?slug=${ev.slug}" class="btn btn-primary-event btn-sm">
                                            View Details <i class="bi bi-arrow-right ms-1"></i>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Event catalog navigation" class="mb-5">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/events?page=${currentPage - 1}&query=${query}&category=${selectedCategoryId}&location=${location}">
                                    Previous
                                </a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <li class="page-item ${currentPage == p ? 'active' : ''}">
                                    <a class="page-link" href="${pageContext.request.contextPath}/events?page=${p}&query=${query}&category=${selectedCategoryId}&location=${location}">
                                        ${p}
                                    </a>
                                </li>
                            </c:forEach>
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/events?page=${currentPage + 1}&query=${query}&category=${selectedCategoryId}&location=${location}">
                                    Next
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </c:when>
            <c:otherwise>
                <!-- Empty Search State -->
                <div class="empty-state my-5">
                    <div class="empty-state-icon">
                        <i class="bi bi-search text-muted"></i>
                    </div>
                    <h3 class="fw-bold mb-2">No Matching Events Found</h3>
                    <p class="text-muted mb-4">We couldn't find any events matching your criteria. Try adjusting your search query, location, or selected category.</p>
                    <a href="${pageContext.request.contextPath}/events" class="btn btn-primary-event">
                        <i class="bi bi-arrow-counterclockwise me-1"></i> Reset Filters &amp; View All
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
