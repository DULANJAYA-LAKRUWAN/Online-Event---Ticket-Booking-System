# EventCart — Online Event & Ticket Booking System

> **Tagline:** Discover. Book. Experience.  
> **Course:** Web Programming II Assessment  
> **Architecture:** Model-View-Controller (MVC)  
> **Implementation Stack:** Java (Jakarta EE 10 / Servlet 6.0) + JSP + JSTL + Hibernate ORM 6 + MySQL 8  

---

## 1. Project Overview

**EventCart** is an enterprise-grade academic web application engineered for online event discovery, ticket shopping, booking management, and test payment workflows. The platform serves three distinct user personas:

1. **Customers (Attendees):** Discover upcoming concerts, tech conferences, sports tournaments, and theater performances; search and filter dynamically; add tickets to cart; checkout via test payment gateway; receive digital tickets with QR references and confirmation emails.
2. **Event Organizers:** Create and manage event listings, configure multi-tiered ticketing (e.g., General Admission, VIP, Early Bird), upload media assets, and monitor sales.
3. **System Administrators:** Supervise categories, audit user accounts, monitor system health, and manage platform governance.

---

## 2. Mandatory Assessment Requirements Coverage

| # | Requirement | Implementation Strategy in EventCart | Status |
|---|---|---|---|
| 1 | Industrial Scenario | Real-world event discovery, multi-tier ticketing, and reservation platform | Implemented |
| 2 | Minimum 5 Business Processes | 1. User Registration & Auth, 2. Event Discovery & Search, 3. Ticket Selection & Cart, 4. Booking & Mock Payment, 5. Ticket Issuance & Email Dispatch | Foundation Ready |
| 3 | Appropriate HTML Elements | Semantic HTML5 (`<header>`, `<nav>`, `<main>`, `<section>`, `<article>`, `<form>`, `<select>`) | Implemented |
| 4 | Modern Responsive Design | Bootstrap 5.3 + Custom CSS Design System (`style.css`), mobile-first flexbox & grid | Implemented |
| 5 | User-friendly Navigation | Sticky navbar, breadcrumb trail, quick category chips, session-aware dropdown | Implemented |
| 6 | Authentication & Authorization | Role-based security (ADMIN, ORGANIZER, CUSTOMER), BCrypt hashing, `AuthenticationFilter` & `AdminAuthorizationFilter` | Implemented |
| 7 | HTTP Sessions | Stateful session management, Session Fixation protection, HttpOnly cookie configuration | Implemented |
| 8 | AJAX Asynchronous Operations | Fetch API wrapper in `main.js`, JSON endpoints via `BaseServlet.sendJson` | Implemented |
| 9 | JSP + Servlets CRUD | Pure MVC: Servlets as Controllers, JSP for presentation, JSTL/EL rendering | Implemented |
| 10 | Hibernate ORM | Hibernate ORM 6 with Jakarta Persistence 3.1, Singleton `HibernateUtil`, transaction management | Implemented |
| 11 | MySQL 8 | Relational schema with InnoDB, foreign key constraints, indexes, UTF8MB4 charset | Implemented |
| 12 | Shopping Cart | Session-backed ticket reservation cart with AJAX operations and badge | Implemented (Phase 03) |
| 13 | Pagination | Server-side pagination query in `GenericDaoImpl` (`findPaginated`) | Implemented |
| 14 | Advanced Search | Multi-parameter search (keyword, category, location, date range) | Implemented |
| 15 | File Uploader | Servlet Multipart config for banner images and receipts | Implemented |
| 16 | Payment Gateway | Mock sandbox payment workflow supporting payment status transitions | Phase 03 Foundation Ready / Phase 04 |
| 17 | Multimedia Elements | Audio preview player and teaser video containers for events | Foundation Ready |
| 18 | SMTP Email | Jakarta Mail integration for async booking and ticket dispatches | Foundation Ready |
| 19 | Uploading Mechanisms | Dedicated asset directories (`assets/images`, `assets/audio`, `assets/video`) | Implemented |
| 20 | HTTP Error Handling | Global custom error views for 400, 401, 403, 404, 405, 500 without stack-trace leakage | Implemented |

---

## 3. Technology Stack

- **Backend:** Java 17/21 LTS, Jakarta Servlet API 6.0, Jakarta Server Pages 3.1, JSTL 3.0 (GlassFish implementation)
- **Persistence:** Hibernate ORM 6.4.4.Final, Jakarta Persistence 3.1
- **Database:** MySQL 8.x
- **Frontend:** HTML5, CSS3, Bootstrap 5.3 CDN, Bootstrap Icons CDN, Vanilla JavaScript (ES6+)
- **Security:** BCrypt password hashing (`jbcrypt`), Session Fixation defenses, XSS sanitization, HttpOnly cookies
- **Serialization:** Google Gson 2.10.1
- **Logging:** SLF4J 2.0.12 + Logback 1.5.3
- **Email:** Jakarta Mail 2.1.3 + Angus Mail 2.0.3
- **Build Tool:** Apache Maven 3.9+
- **Application Server:** Apache Tomcat 10.1+ (or Payara 7 / GlassFish 7)

---

## 4. Architecture & Directory Structure

EventCart strictly adheres to the standard enterprise MVC architecture:

```
Browser (JSP, HTML5, Bootstrap 5, AJAX Fetch)
   │
   ▼
[Servlet Layer / Controllers] (BaseServlet, HomeController, LoginServlet, RegisterServlet)
   │
   ▼
[Service Layer] (UserService, EventService, BookingService)
   │
   ▼
[DAO / Repository Layer] (GenericDao, UserDao, EventDao)
   │
   ▼
[Hibernate ORM 6 & SessionFactory] (HibernateUtil, AppConfig)
   │
   ▼
[MySQL 8 Database]
```

### Project Directory Layout:

```
EventCart/
├── pom.xml                                 # Maven configuration
├── README.md                               # Project documentation
│
├── database/
│   └── eventcart.sql                       # Complete MySQL 8 DDL & Seed Data
│
└── src/
    ├── main/
    │   ├── java/com/eventcart/
    │   │   ├── config/                     # Centralized AppConfig with Env overrides
    │   │   ├── controller/                 # BaseServlet and Controllers
    │   │   │   └── auth/                   # Login, Register, Logout Servlets
    │   │   ├── dao/                        # GenericDao and entity DAO interfaces
    │   │   │   └── impl/                   # Hibernate DAO implementations
    │   │   ├── dto/                        # ApiResponse and DTOs
    │   │   ├── entity/                     # JPA Entities (User, Role, BaseEntity)
    │   │   ├── exception/                  # Custom application exceptions
    │   │   ├── filter/                     # CharacterEncoding, Authentication, AdminAuthorization
    │   │   ├── service/                    # Business service interfaces
    │   │   │   └── impl/                   # Service implementations
    │   │   └── util/                       # HibernateUtil, PasswordUtil, ValidationUtil, JsonUtil
    │   │
    │   ├── resources/
    │   │   ├── application.properties      # Application & database settings
    │   │   ├── hibernate.cfg.xml           # Hibernate 6 configuration
    │   │   └── logback.xml                 # Logging rules
    │   │
    │   └── webapp/
    │       ├── assets/
    │       │   ├── css/style.css           # EventCart core CSS design system
    │       │   ├── js/main.js              # AJAX request wrapper & UI notifications
    │       │   ├── images/                 # Uploaded event media
    │       │   ├── audio/                  # Audio previews
    │       │   └── video/                  # Promotional videos
    │       │
    │       ├── WEB-INF/
    │       │   ├── web.xml                 # Servlet 6 deployment descriptor
    │       │   └── views/
    │       │       ├── common/             # Reusable header, navbar, footer, alerts
    │       │       └── error/              # Custom 400, 401, 403, 404, 405, 500 error pages
    │       │
    │       ├── index.jsp                   # Landing & discovery showcase
    │       ├── login.jsp                   # Authentication page
    │       └── register.jsp                # Account registration page
    │
    └── test/
        └── java/com/eventcart/util/        # Unit tests for PasswordUtil & ValidationUtil
```

---

## 5. Setup & Configuration

### Prerequisites
1. **JDK 17 or JDK 21** installed (`JAVA_HOME` configured).
2. **Apache Maven 3.8+** installed.
3. **MySQL 8.0+** running locally on port 3306.
4. **Apache Tomcat 10.1+** (compatible with Jakarta EE 10 / Servlet 6.0).

### Database Initialization
1. Start your local MySQL server.
2. Execute the schema script using MySQL CLI or MySQL Workbench:
   ```bash
   mysql -u root -p < database/eventcart.sql
   ```
3. Verify that the database `eventcart_db` and tables (`users`, `categories`, `events`, `ticket_types`, `bookings`, `booking_items`, `payments`, `reviews`) have been created with initial seed records.

### Environment Configuration
The application reads settings from `src/main/resources/application.properties`. You can override any configuration at runtime using environment variables:

| Setting | Environment Variable | Default Value | Description |
|---|---|---|---|
| `db.url` | `DB_URL` | `jdbc:mysql://localhost:3306/eventcart_db...` | MySQL connection JDBC URL |
| `db.username` | `DB_USERNAME` | `root` | MySQL user account |
| `db.password` | `DB_PASSWORD` | `(empty)` | MySQL user password |
| `mail.smtp.host` | `SMTP_HOST` | `smtp.gmail.com` | SMTP host for emails |
| `mail.smtp.username`| `SMTP_USERNAME` | `noreply.eventcart@example.com` | SMTP username |
| `mail.smtp.password`| `SMTP_PASSWORD` | `(empty)` | SMTP application password |

---

## 6. Build & Packaging

Build the project and produce the standard `.war` artifact:

```bash
# Clean, compile, execute unit tests, and build WAR file:
mvn clean package
```

The resulting deployment archive will be generated at:
```
target/eventcart.war
```

To run unit tests only:
```bash
mvn test
```

---

## 7. Deployment Instructions (Apache Tomcat 10.1+)

1. Build the WAR package using `mvn clean package`.
2. Copy `target/eventcart.war` into your Tomcat `webapps/` directory:
   ```powershell
   Copy-Item "target\eventcart.war" -Destination "C:\tools\apache-tomcat-10.1.60\webapps\"
   ```
3. Start Apache Tomcat:
   ```powershell
   & "C:\tools\apache-tomcat-10.1.60\bin\startup.bat"
   ```
4. Access the application in your browser:
   ```
   http://localhost:8080/eventcart/
   ```

---

## 8. Academic Demonstration Credentials

For examiners and demonstration grading, default accounts are seeded in `database/eventcart.sql`:

| Role | Email | Password | Permissions |
|---|---|---|---|
| **System Admin** | `admin@eventcart.com` | `Admin@123` | Full administrative console access (`/admin/*`) |
| **Organizer** | `organizer@eventcart.com` | `Organizer@123` | Event creation & ticket management (`/organizer/*`) |
| **Customer** | `user@eventcart.com` | `User@123` | Event discovery, cart checkout, and ticket booking |

*Note: All passwords are authenticated using BCrypt (12 work rounds) and plain text passwords are never stored.*

---

## 9. Current Phase Status & Next Milestones

### Phase 01: Project Foundation (Completed)
- [x] Clean Jakarta EE 10 / Servlet 6.0 / Hibernate 6 / MySQL 8 structure
- [x] Singleton `HibernateUtil` with safe transaction closures
- [x] Global filters (`CharacterEncodingFilter`, `AuthenticationFilter`, `AdminAuthorizationFilter`)
- [x] Security utilities (`PasswordUtil` with BCrypt, `ValidationUtil`, `JsonUtil`)
- [x] HTTP Error Handling for 400, 401, 403, 404, 405, 500
- [x] Responsive Bootstrap 5 UI design system & layout fragments
- [x] Full compilation and automated unit tests passing (`mvn clean package`)

### Phase 02: Event & Category Management (Completed)
- [x] Category domain model, DAO, and Service with unique slug generator and status management
- [x] Event domain model, DAO, and Service with publishing constraints and date/time validation
- [x] TicketType domain model, DAO, and Service with inventory stock control
- [x] Admin category management interface (`/admin/categories`) with AJAX status toggle
- [x] Admin event listings interface (`/admin/events`) with quick status updates and actions
- [x] Multipart event banner image uploader (`FileUploadUtil`) with 5MB cap and MIME/extension restrictions
- [x] Admin ticket tiers management interface (`/admin/events/tickets`)
- [x] Public customer event catalog (`/events`) with search, category filtering, and pagination
- [x] Public event details page (`/event`) with rich metadata and prepared ticket tier selection
- [x] Real database-backed `index.jsp` homepage with dynamic featured event showcase
- [x] Database seed script (`database/eventcart.sql`) with 6 categories, 10 events, and multi-tier tickets
- [x] 18 automated unit tests passing with zero failures

### Phase 03: Ticket Shopping Cart & Booking Workflow (Next Step)
- Session-based Ticket Shopping Cart (AJAX add/update/remove items)
- Ticket inventory reservation locking mechanism
- Multi-step checkout workflow with customer contact details
- Mock / Sandbox Payment Gateway integration
- Digital E-Ticket generation with cryptographic QR hash
- SMTP confirmation email dispatching with Jakarta Mail
