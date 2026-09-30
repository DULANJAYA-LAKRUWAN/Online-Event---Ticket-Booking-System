-- =====================================================================
-- EventCart - Online Event & Ticket Booking System
-- Database Schema Definition & Initial Seed Data (Phase 02 Updated)
-- Target Database: MySQL 8.x
-- Character Set: utf8mb4 | Collation: utf8mb4_unicode_ci
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `eventcart_db`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `eventcart_db`;

-- ---------------------------------------------------------------------
-- 1. Table: users
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(150) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(120) NOT NULL,
    `phone_number` VARCHAR(30) NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    `active` TINYINT(1) NOT NULL DEFAULT 1,
    `profile_image` VARCHAR(255) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    KEY `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. Table: categories
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `slug` VARCHAR(120) NOT NULL,
    `description` VARCHAR(500) NULL,
    `icon_class` VARCHAR(50) NULL DEFAULT 'bi-tag',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_categories_name` (`name`),
    UNIQUE KEY `uk_categories_slug` (`slug`),
    KEY `idx_categories_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. Table: events
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `events` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `organizer_id` BIGINT NULL,
    `category_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `slug` VARCHAR(220) NOT NULL,
    `description` TEXT NOT NULL,
    `venue` VARCHAR(150) NOT NULL,
    `location` VARCHAR(150) NOT NULL,
    `event_date` DATE NOT NULL,
    `event_time` TIME NOT NULL,
    `banner_image` VARCHAR(255) NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    `featured` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_events_slug` (`slug`),
    KEY `idx_events_organizer` (`organizer_id`),
    KEY `idx_events_category` (`category_id`),
    KEY `idx_events_date` (`event_date`),
    KEY `idx_events_status` (`status`),
    KEY `idx_events_featured` (`featured`),
    CONSTRAINT `fk_events_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_events_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. Table: ticket_types
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ticket_types` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `event_id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(300) NULL,
    `price` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `total_quantity` INT NOT NULL,
    `available_quantity` INT NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ticket_types_event` (`event_id`),
    KEY `idx_ticket_types_status` (`status`),
    CONSTRAINT `fk_ticket_types_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5. Table: bookings (Future booking schema foundation)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `bookings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `booking_reference` VARCHAR(40) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10, 2) NOT NULL,
    `discount_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `final_amount` DECIMAL(10, 2) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `qr_code_hash` VARCHAR(100) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bookings_reference` (`booking_reference`),
    KEY `idx_bookings_user` (`user_id`),
    KEY `idx_bookings_status` (`status`),
    CONSTRAINT `fk_bookings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 6. Table: booking_items
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `booking_id` BIGINT NOT NULL,
    `ticket_type_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(10, 2) NOT NULL,
    `subtotal` DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_bitems_booking` (`booking_id`),
    KEY `idx_bitems_ticket` (`ticket_type_id`),
    CONSTRAINT `fk_bitems_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_bitems_ticket` FOREIGN KEY (`ticket_type_id`) REFERENCES `ticket_types` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 7. Table: payments
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `booking_id` BIGINT NOT NULL,
    `payment_reference` VARCHAR(50) NOT NULL,
    `transaction_id` VARCHAR(100) NULL,
    `payment_method` VARCHAR(30) NOT NULL DEFAULT 'MOCK_SANDBOX',
    `amount` DECIMAL(10, 2) NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    `payment_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `response_payload` TEXT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payments_reference` (`payment_reference`),
    KEY `idx_payments_booking` (`booking_id`),
    CONSTRAINT `fk_payments_booking` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 8. Table: reviews
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `reviews` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `event_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `rating` INT NOT NULL,
    `comment` TEXT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_reviews_event` (`event_id`),
    KEY `idx_reviews_user` (`user_id`),
    CONSTRAINT `fk_reviews_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- =====================================================================
-- INITIAL SEED DATA (Academic Assessment Demo)
-- =====================================================================

-- Seed 1: Users (Passwords hashed using BCrypt log rounds 12)
-- admin@eventcart.com     -> Admin@123
-- organizer@eventcart.com -> Organizer@123
-- user@eventcart.com      -> User@123
INSERT INTO `users` (`id`, `email`, `password_hash`, `full_name`, `phone_number`, `role`, `active`, `created_at`, `updated_at`)
VALUES
(1, 'admin@eventcart.com', '$2a$12$mZPabMaLIUSVPD..ETn8oOAAVO51M6FOhXCpV80Gt//xRFJQemuV.', 'System Administrator', '+1 555-0199', 'ADMIN', 1, NOW(), NOW()),
(2, 'organizer@eventcart.com', '$2a$12$xfHFamipQL4p56cyMbTfO.TSyBtAEHKe9oBqSS8fTFZHyY6zNh0Bm', 'Global Concert Productions', '+1 555-0145', 'ORGANIZER', 1, NOW(), NOW()),
(3, 'user@eventcart.com', '$2a$12$WVVc9P.ZuwatJV6T54c.TOSvJsa3a88Cb8qA0snuJkcJC10Y4mrMy', 'John Attendee', '+1 555-0178', 'CUSTOMER', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `email`=`email`;

-- Seed 2: 6 Categories
INSERT INTO `categories` (`id`, `name`, `slug`, `description`, `icon_class`, `status`)
VALUES
(1, 'Music & Concerts', 'music-concerts', 'Live music festivals, arena tours, and acoustic symphonies', 'bi-music-note-beamed', 'ACTIVE'),
(2, 'Technology & AI', 'technology-ai', 'Developer summits, AI conferences, and tech expos', 'bi-cpu', 'ACTIVE'),
(3, 'Sports & Fitness', 'sports-fitness', 'Championship matches, tournaments, and marathons', 'bi-trophy', 'ACTIVE'),
(4, 'Arts & Theater', 'arts-theater', 'Stage drama, broadway musicals, and gallery showcases', 'bi-palette', 'ACTIVE'),
(5, 'Business & Networking', 'business-networking', 'Founder keynotes, venture capital summits, and career forums', 'bi-briefcase', 'ACTIVE'),
(6, 'Food & Culinary Festivals', 'food-festivals', 'Gourmet street food festivals, wine tastings, and culinary showcases', 'bi-cup-hot', 'ACTIVE')
ON DUPLICATE KEY UPDATE `name`=`name`;

-- Seed 3: 10 Realistic Events
INSERT INTO `events` (`id`, `organizer_id`, `category_id`, `title`, `slug`, `description`, `venue`, `location`, `event_date`, `event_time`, `banner_image`, `status`, `featured`)
VALUES
(1, 2, 1, 'Acoustic Echoes & Symphony Live 2026', 'acoustic-echoes-symphony-2026', 
 'An electrifying evening featuring world-renowned acoustic instrumentalists and modern philharmonic orchestral arrangements. Experience pure sonic excellence with state-of-the-art acoustic mapping and VIP stage viewing.', 
 'Grand Convention Hall', 'New York, NY', '2026-11-14', '19:30:00', NULL, 'PUBLISHED', 1),

(2, 2, 2, 'International AI & Web 3.0 Summit', 'international-ai-web-summit-2026', 
 'Discover cutting-edge enterprise AI innovations, keynote panels from leading tech visionaries, and hands-on developer workshops. Over 2,000 engineers and leaders exploring autonomous agents and modern architectures.', 
 'Silicon Center, Auditorium 4', 'San Francisco, CA', '2026-12-03', '09:00:00', NULL, 'PUBLISHED', 1),

(3, 2, 3, 'National Basketball Final Showdown', 'national-basketball-final-2026', 
 'Witness the grand culmination of the basketball season as top contenders battle for the championship trophy in high-stakes action. Features court-side entertainment and halftime performances.', 
 'National Stadium', 'Chicago, IL', '2026-12-20', '18:00:00', NULL, 'PUBLISHED', 1),

(4, 2, 4, 'Broadway Drama: The Phantom of Venice', 'broadway-phantom-venice-2026', 
 'A spellbinding theatrical masterpiece featuring award-winning stage performers, grand costume designs, and orchestral live accompaniment in an intimate theater setting.', 
 'Royal Playhouse Theater', 'Boston, MA', '2026-11-28', '20:00:00', NULL, 'PUBLISHED', 1),

(5, 2, 5, 'Global Founders & Venture Capital Forum', 'global-founders-vc-forum-2026', 
 'Connect with over 50 top-tier venture capitalists, angel investors, and high-growth technology startup founders. Includes curated pitch sessions and executive networking luncheons.', 
 'Crown Plaza Executive Ballroom', 'Austin, TX', '2026-12-08', '08:30:00', NULL, 'PUBLISHED', 0),

(6, 2, 6, 'International Street Food & Wine Fest', 'street-food-wine-fest-2026', 
 'Sample exquisite cuisines crafted by Michelin-star guest chefs alongside local culinary artisans. Over 80 craft tasting stalls with live acoustic music and chef cooking demonstrations.', 
 'Riverside Promenade Gardens', 'Seattle, WA', '2026-11-07', '11:00:00', NULL, 'PUBLISHED', 0),

(7, 2, 1, 'Neon Sunset Electronic Music Festival', 'neon-sunset-electronic-fest-2026', 
 'An outdoor summer festival spectacle with multiple stages, world-class DJ headliners, laser visual spectacles, and interactive festival art installations.', 
 'Bayfront Open Air Park', 'Miami, FL', '2026-12-31', '16:00:00', NULL, 'PUBLISHED', 1),

(8, 2, 2, 'Cloud Security & DevOps World Congress', 'cloud-security-devops-congress-2026', 
 'Deep dive into zero-trust security architectures, Kubernetes orchestration at scale, and automated DevSecOps pipelines. Interactive lab stations and technical certification workshops.', 
 'Tech City Convention Center', 'Atlanta, GA', '2026-11-18', '09:30:00', NULL, 'PUBLISHED', 0),

(9, 2, 3, 'City Marathon & 10K Championship Run', 'city-marathon-10k-run-2026', 
 'Join thousands of fitness enthusiasts running across the historic city center and waterfront. Finisher medals, timing chips, and post-race wellness recovery village included.', 
 'Downtown City Plaza', 'Denver, CO', '2026-11-22', '06:30:00', NULL, 'PUBLISHED', 0),

(10, 2, 4, 'Contemporary Modern Art Exhibition & Gala', 'modern-art-gala-2026', 
 'Exclusive opening night preview of curated contemporary sculptures, digital NFT galleries, and fine art collections with artist keynote panels and champagne reception.', 
 'Metropolitan Art Gallery', 'Los Angeles, CA', '2026-12-12', '19:00:00', NULL, 'DRAFT', 0)
ON DUPLICATE KEY UPDATE `slug`=`slug`;

-- Seed 4: At least 2 Ticket Types for each published event
INSERT INTO `ticket_types` (`id`, `event_id`, `name`, `description`, `price`, `total_quantity`, `available_quantity`, `status`)
VALUES
-- Event 1: Acoustic Echoes
(1, 1, 'General Admission', 'Standard seating in main auditorium with clear acoustics', 49.00, 300, 280, 'ACTIVE'),
(2, 1, 'VIP Golden Circle', 'Front-row seating + exclusive soundcheck access & VIP lounge pass', 120.00, 50, 42, 'ACTIVE'),

-- Event 2: AI Summit
(3, 2, 'Standard Attendee Pass', 'Full access to keynote panels and exhibition floor', 79.00, 500, 480, 'ACTIVE'),
(4, 2, 'All-Access VIP & Workshop Pass', 'Full access + hands-on technical code labs and executive networking luncheon', 199.00, 100, 95, 'ACTIVE'),

-- Event 3: Basketball Final
(5, 3, 'Upper Deck Seating', 'Upper tier stadium seating with panoramic arena view', 35.00, 800, 750, 'ACTIVE'),
(6, 3, 'Courtside Premium', 'First-row courtside seat with exclusive halftime lounge access', 250.00, 60, 48, 'ACTIVE'),

-- Event 4: Broadway Phantom
(7, 4, 'Balcony Tier', 'Elevated view of the grand stage', 55.00, 200, 190, 'ACTIVE'),
(8, 4, 'Orchestra Front Stalls', 'Prime center seating with complimentary theater program', 135.00, 80, 74, 'ACTIVE'),

-- Event 5: Founders Forum
(9, 5, 'Founder Delegate', 'Access to pitch stages and networking halls', 89.00, 250, 230, 'ACTIVE'),
(10, 5, 'Investor VIP Pass', 'Access to private meeting suites and deal-flow directory', 299.00, 50, 45, 'ACTIVE'),

-- Event 6: Food Festival
(11, 6, 'Tasting Pass (5 Tokens)', 'Entry + 5 gourmet food tasting tokens', 29.00, 400, 380, 'ACTIVE'),
(12, 6, 'Gourmet Unlimited VIP', 'Entry + unlimited tastings and sommelier wine pairings', 79.00, 100, 90, 'ACTIVE'),

-- Event 7: Neon Sunset Festival
(13, 7, 'General Festival Pass', 'Full festival grounds and stage access', 65.00, 1000, 920, 'ACTIVE'),
(14, 7, 'VIP Sky Deck', 'Elevated viewing platform, private bar, and expedited entry', 160.00, 150, 130, 'ACTIVE'),

-- Event 8: Cloud Security Congress
(15, 8, 'Conference Pass', 'Access to all security tracks and vendor floor', 95.00, 300, 285, 'ACTIVE'),
(16, 8, 'Workshop Bundle', 'Conference pass + 1 hands-on cloud lab certification session', 185.00, 80, 72, 'ACTIVE'),

-- Event 9: City Marathon
(17, 9, '10K Run Entry', 'Bib, timing chip, event technical t-shirt, and finisher medal', 40.00, 500, 460, 'ACTIVE'),
(18, 9, 'Full Marathon 42K', 'Full marathon registration with pace group guide and hydration support', 65.00, 600, 540, 'ACTIVE')
ON DUPLICATE KEY UPDATE `name`=`name`;
