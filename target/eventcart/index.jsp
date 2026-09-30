<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="EventCart — Discover. Book. Experience." scope="request" />
<c:set var="pageActive" value="home" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jspf" %>
<%@ include file="/WEB-INF/views/common/navbar.jspf" %>

<main>
    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container position-relative">
            <div class="row align-items-center">
                <div class="col-lg-8 mx-auto text-center">
                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle px-3 py-2 rounded-pill fw-semibold mb-3">
                        <i class="bi bi-stars me-1"></i> Next-Gen Ticket Booking Platform
                    </span>
                    <h1 class="hero-title mb-3">
                        Discover. Book. <span class="hero-highlight">Experience.</span>
                    </h1>
                    <p class="hero-lead mx-auto mb-4">
                        Explore premier music concerts, technology summits, sports tournaments, and performing arts. Book authenticated tickets effortlessly in seconds.
                    </p>

                    <!-- Advanced Search Bar Foundation -->
                    <div class="search-bar-box mb-4 text-start">
                        <form action="${pageContext.request.contextPath}/events" method="get" class="row g-2 align-items-center">
                            <div class="col-md-5">
                                <div class="input-group">
                                    <span class="input-group-text bg-white border-0 text-muted"><i class="bi bi-search"></i></span>
                                    <input type="text" name="query" class="form-control border-0 ps-0" placeholder="Search event title, artist, or keyword...">
                                </div>
                            </div>
                            <div class="col-md-3 border-start-md">
                                <div class="input-group">
                                    <span class="input-group-text bg-white border-0 text-muted"><i class="bi bi-grid"></i></span>
                                    <select name="category" class="form-select border-0 ps-0">
                                        <option value="">All Categories</option>
                                        <option value="music">Music &amp; Concerts</option>
                                        <option value="tech">Technology &amp; AI</option>
                                        <option value="sports">Sports &amp; Fitness</option>
                                        <option value="arts">Arts &amp; Theater</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-2 border-start-md">
                                <div class="input-group">
                                    <span class="input-group-text bg-white border-0 text-muted"><i class="bi bi-geo-alt"></i></span>
                                    <input type="text" name="location" class="form-control border-0 ps-0" placeholder="City / Venue">
                                </div>
                            </div>
                            <div class="col-md-2">
                                <button type="submit" class="btn btn-primary-event w-100 py-2">
                                    <i class="bi bi-search me-1"></i> Find
                                </button>
                            </div>
                        </form>
                    </div>

                    <!-- Popular Category Chips -->
                    <div class="d-flex flex-wrap justify-content-center gap-2 pt-2">
                        <span class="text-secondary small align-self-center me-1">Popular:</span>
                        <a href="#featured-events" class="badge text-bg-dark rounded-pill px-3 py-2 text-decoration-none border border-secondary">
                            <i class="bi bi-music-note-beamed me-1 text-info"></i> Music
                        </a>
                        <a href="#featured-events" class="badge text-bg-dark rounded-pill px-3 py-2 text-decoration-none border border-secondary">
                            <i class="bi bi-cpu me-1 text-warning"></i> Tech Summits
                        </a>
                        <a href="#featured-events" class="badge text-bg-dark rounded-pill px-3 py-2 text-decoration-none border border-secondary">
                            <i class="bi bi-trophy me-1 text-success"></i> Sports
                        </a>
                        <a href="#featured-events" class="badge text-bg-dark rounded-pill px-3 py-2 text-decoration-none border border-secondary">
                            <i class="bi bi-palette me-1 text-danger"></i> Theater
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Global Alerts Section -->
    <div class="container mt-3">
        <%@ include file="/WEB-INF/views/common/alerts.jspf" %>
    </div>

    <!-- Value Proposition Features -->
    <section class="py-5 bg-white border-bottom">
        <div class="container">
            <div class="row g-4 text-center">
                <div class="col-md-4">
                    <div class="p-3">
                        <div class="d-inline-flex p-3 rounded-circle bg-primary-subtle text-primary mb-3 fs-3">
                            <i class="bi bi-lightning-charge-fill"></i>
                        </div>
                        <h5 class="fw-bold mb-2">Instant Ticket Issuance</h5>
                        <p class="text-muted small mb-0">Receive cryptographically verified digital QR tickets immediately upon booking confirmation.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="p-3">
                        <div class="d-inline-flex p-3 rounded-circle bg-success-subtle text-success mb-3 fs-3">
                            <i class="bi bi-shield-check"></i>
                        </div>
                        <h5 class="fw-bold mb-2">Secure Transactions</h5>
                        <p class="text-muted small mb-0">Protected checkout workflow with enterprise-grade payment and transaction integrity.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="p-3">
                        <div class="d-inline-flex p-3 rounded-circle bg-info-subtle text-info mb-3 fs-3">
                            <i class="bi bi-envelope-paper-heart"></i>
                        </div>
                        <h5 class="fw-bold mb-2">Real-time SMTP Alerts</h5>
                        <p class="text-muted small mb-0">Instant booking confirmations and event updates delivered straight to your inbox.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Featured Events Showcase -->
    <section id="featured-events" class="py-5">
        <div class="container">
            <div class="d-flex justify-content-between align-items-end mb-4">
                <div>
                    <span class="text-primary fw-bold text-uppercase small letter-spacing-1">Trending Experiences</span>
                    <h2 class="fw-bold mb-0">Featured Upcoming Events</h2>
                </div>
                <a href="${pageContext.request.contextPath}/#featured-events" class="btn btn-outline-dark btn-sm rounded-pill px-3">
                    View All Events <i class="bi bi-arrow-right ms-1"></i>
                </a>
            </div>

            <div class="row g-4">
                <!-- Event Card 1 -->
                <div class="col-lg-4 col-md-6">
                    <div class="event-card">
                        <div class="event-card-img-wrap">
                            <!-- High quality event placeholder visual -->
                            <svg class="event-card-img" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img">
                                <defs>
                                    <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                                        <stop offset="0%" style="stop-color:#312e81;stop-opacity:1" />
                                        <stop offset="100%" style="stop-color:#4338ca;stop-opacity:1" />
                                    </linearGradient>
                                </defs>
                                <rect width="100%" height="100%" fill="url(#grad1)"/>
                                <text x="50%" y="45%" fill="#e0e7ff" font-family="Inter, sans-serif" font-weight="700" font-size="20" text-anchor="middle">SUMMER FEST 2026</text>
                                <text x="50%" y="65%" fill="#a5b4fc" font-family="Inter, sans-serif" font-size="14" text-anchor="middle">Live Arena Tour</text>
                            </svg>
                            <span class="event-badge-category">Music Concert</span>
                            <span class="event-badge-price">From $49.00</span>
                        </div>
                        <div class="event-card-body">
                            <h5 class="event-card-title">Acoustic Echoes &amp; Symphony Live 2026</h5>
                            <div class="event-card-meta">
                                <i class="bi bi-calendar3 text-primary"></i>
                                <span>Saturday, Nov 14, 2026 &bull; 7:30 PM</span>
                            </div>
                            <div class="event-card-meta mb-3">
                                <i class="bi bi-geo-alt text-danger"></i>
                                <span>Grand Convention Hall, Metro Arena</span>
                            </div>
                            <p class="text-muted small flex-grow-1">
                                An electrifying evening featuring world-renowned acoustic instrumentalists and modern philharmonic orchestral arrangements.
                            </p>
                            <div class="pt-2 border-top d-flex justify-content-between align-items-center">
                                <span class="badge bg-success-subtle text-success border border-success-subtle">
                                    <i class="bi bi-ticket-detailed me-1"></i> Tickets Available
                                </span>
                                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary-event btn-sm">
                                    Book Now <i class="bi bi-chevron-right ms-1"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Event Card 2 -->
                <div class="col-lg-4 col-md-6">
                    <div class="event-card">
                        <div class="event-card-img-wrap">
                            <svg class="event-card-img" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img">
                                <defs>
                                    <linearGradient id="grad2" x1="0%" y1="0%" x2="100%" y2="100%">
                                        <stop offset="0%" style="stop-color:#0f172a;stop-opacity:1" />
                                        <stop offset="100%" style="stop-color:#0f766e;stop-opacity:1" />
                                    </linearGradient>
                                </defs>
                                <rect width="100%" height="100%" fill="url(#grad2)"/>
                                <text x="50%" y="45%" fill="#ccfbf1" font-family="Inter, sans-serif" font-weight="700" font-size="20" text-anchor="middle">GLOBAL AI EXPO</text>
                                <text x="50%" y="65%" fill="#5eead4" font-family="Inter, sans-serif" font-size="14" text-anchor="middle">Future of Autonomous Web</text>
                            </svg>
                            <span class="event-badge-category">Technology</span>
                            <span class="event-badge-price">From $79.00</span>
                        </div>
                        <div class="event-card-body">
                            <h5 class="event-card-title">International AI &amp; Web 3.0 Summit</h5>
                            <div class="event-card-meta">
                                <i class="bi bi-calendar3 text-primary"></i>
                                <span>Thursday, Dec 03, 2026 &bull; 9:00 AM</span>
                            </div>
                            <div class="event-card-meta mb-3">
                                <i class="bi bi-geo-alt text-danger"></i>
                                <span>Silicon Center, Auditorium 4</span>
                            </div>
                            <p class="text-muted small flex-grow-1">
                                Discover cutting-edge enterprise AI innovations, keynote panels from leading tech visionaries, and hands-on developer workshops.
                            </p>
                            <div class="pt-2 border-top d-flex justify-content-between align-items-center">
                                <span class="badge bg-warning-subtle text-warning border border-warning-subtle">
                                    <i class="bi bi-fire me-1"></i> Fast Selling
                                </span>
                                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary-event btn-sm">
                                    Book Now <i class="bi bi-chevron-right ms-1"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Event Card 3 -->
                <div class="col-lg-4 col-md-6">
                    <div class="event-card">
                        <div class="event-card-img-wrap">
                            <svg class="event-card-img" width="100%" height="100%" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice" focusable="false" role="img">
                                <defs>
                                    <linearGradient id="grad3" x1="0%" y1="0%" x2="100%" y2="100%">
                                        <stop offset="0%" style="stop-color:#7f1d1d;stop-opacity:1" />
                                        <stop offset="100%" style="stop-color:#991b1b;stop-opacity:1" />
                                    </linearGradient>
                                </defs>
                                <rect width="100%" height="100%" fill="url(#grad3)"/>
                                <text x="50%" y="45%" fill="#fee2e2" font-family="Inter, sans-serif" font-weight="700" font-size="20" text-anchor="middle">CHAMPIONSHIP 2026</text>
                                <text x="50%" y="65%" fill="#fca5a5" font-family="Inter, sans-serif" font-size="14" text-anchor="middle">National Arena Finals</text>
                            </svg>
                            <span class="event-badge-category">Sports</span>
                            <span class="event-badge-price">From $35.00</span>
                        </div>
                        <div class="event-card-body">
                            <h5 class="event-card-title">National Basketball Final Showdown</h5>
                            <div class="event-card-meta">
                                <i class="bi bi-calendar3 text-primary"></i>
                                <span>Sunday, Dec 20, 2026 &bull; 6:00 PM</span>
                            </div>
                            <div class="event-card-meta mb-3">
                                <i class="bi bi-geo-alt text-danger"></i>
                                <span>National Stadium, Courtside</span>
                            </div>
                            <p class="text-muted small flex-grow-1">
                                Witness the grand culmination of the basketball season as top contenders battle for the championship trophy in high-stakes action.
                            </p>
                            <div class="pt-2 border-top d-flex justify-content-between align-items-center">
                                <span class="badge bg-success-subtle text-success border border-success-subtle">
                                    <i class="bi bi-ticket-detailed me-1"></i> Tickets Available
                                </span>
                                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary-event btn-sm">
                                    Book Now <i class="bi bi-chevron-right ms-1"></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Assessment Showcase Banner -->
    <section class="py-5 bg-light border-top">
        <div class="container">
            <div class="bg-dark text-white rounded-4 p-5 position-relative overflow-hidden">
                <div class="row align-items-center position-relative" style="z-index: 2;">
                    <div class="col-lg-8">
                        <span class="badge bg-indigo text-light border border-indigo px-3 py-1 rounded-pill mb-2">Web Programming II Architecture</span>
                        <h3 class="fw-bold mb-2">Built for Industrial Reliability &amp; Scalability</h3>
                        <p class="text-secondary mb-0">
                            Equipped with Jakarta Servlet 6.0, Hibernate ORM 6 persistence, role-based authorization, AJAX shopping cart workflow, and responsive client interfaces.
                        </p>
                    </div>
                    <div class="col-lg-4 text-lg-end mt-4 mt-lg-0">
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary-event btn-lg px-4">
                            Get Started Now <i class="bi bi-arrow-right ms-1"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/common/footer.jspf" %>
