-- =====================================================================
-- EventCart - Online Event & Ticket Booking System
-- Database Schema Definition & Initial Seed Data
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
    `name` VARCHAR(80) NOT NULL,
    `slug` VARCHAR(80) NOT NULL,
    `description` VARCHAR(255) NULL,
    `icon_class` VARCHAR(50) NULL,
    `active` TINYINT(1) NOT NULL DEFAULT 1,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_categories_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. Table: events
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `events` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `organizer_id` BIGINT NOT NULL,
    `category_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `slug` VARCHAR(200) NOT NULL,
    `short_description` VARCHAR(300) NOT NULL,
    `full_description` TEXT NULL,
    `banner_image` VARCHAR(255) NULL,
    `venue_name` VARCHAR(150) NOT NULL,
    `venue_address` VARCHAR(255) NOT NULL,
    `city` VARCHAR(100) NOT NULL,
    `start_date_time` DATETIME NOT NULL,
    `end_date_time` DATETIME NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    `is_featured` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_events_slug` (`slug`),
    KEY `idx_events_organizer` (`organizer_id`),
    KEY `idx_events_category` (`category_id`),
    KEY `idx_events_status` (`status`),
    KEY `idx_events_city` (`city`),
    KEY `idx_events_start_date` (`start_date_time`),
    CONSTRAINT `fk_events_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_events_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. Table: ticket_types
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `ticket_types` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `event_id` BIGINT NOT NULL,
    `name` VARCHAR(80) NOT NULL,
    `description` VARCHAR(255) NULL,
    `price` DECIMAL(10, 2) NOT NULL,
    `total_quantity` INT NOT NULL,
    `available_quantity` INT NOT NULL,
    `max_per_booking` INT NOT NULL DEFAULT 5,
    `sales_start` DATETIME NULL,
    `sales_end` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_tickets_event` (`event_id`),
    CONSTRAINT `fk_tickets_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5. Table: bookings
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
-- Password for all seed users is corresponding username/role with standard format:
-- admin@eventcart.com     -> Admin@123
-- organizer@eventcart.com -> Organizer@123
-- user@eventcart.com      -> User@123
INSERT INTO `users` (`id`, `email`, `password_hash`, `full_name`, `phone_number`, `role`, `active`, `created_at`, `updated_at`)
VALUES
(1, 'admin@eventcart.com', '$2a$12$mZPabMaLIUSVPD..ETn8oOAAVO51M6FOhXCpV80Gt//xRFJQemuV.', 'System Administrator', '+1 555-0199', 'ADMIN', 1, NOW(), NOW()),
(2, 'organizer@eventcart.com', '$2a$12$xfHFamipQL4p56cyMbTfO.TSyBtAEHKe9oBqSS8fTFZHyY6zNh0Bm', 'Global Concert Productions', '+1 555-0145', 'ORGANIZER', 1, NOW(), NOW()),
(3, 'user@eventcart.com', '$2a$12$WVVc9P.ZuwatJV6T54c.TOSvJsa3a88Cb8qA0snuJkcJC10Y4mrMy', 'John Attendee', '+1 555-0178', 'CUSTOMER', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `email`=`email`;

-- Seed 2: Categories
INSERT INTO `categories` (`id`, `name`, `slug`, `description`, `icon_class`, `active`)
VALUES
(1, 'Music & Concerts', 'music', 'Live music festivals, arena tours, and acoustic symphonies', 'bi-music-note-beamed', 1),
(2, 'Technology & AI', 'tech', 'Developer summits, AI conferences, and tech expos', 'bi-cpu', 1),
(3, 'Sports & Fitness', 'sports', 'Championship matches, tournaments, and marathons', 'bi-trophy', 1),
(4, 'Arts & Theater', 'arts', 'Stage drama, broadway musicals, and gallery showcases', 'bi-palette', 1),
(5, 'Food & Festivals', 'food', 'Gourmet street food festivals, wine tastings, and culinary showcases', 'bi-cup-hot', 1)
ON DUPLICATE KEY UPDATE `slug`=`slug`;

-- Seed 3: Sample Events
INSERT INTO `events` (`id`, `organizer_id`, `category_id`, `title`, `slug`, `short_description`, `full_description`, `venue_name`, `venue_address`, `city`, `start_date_time`, `end_date_time`, `status`, `is_featured`)
VALUES
(1, 2, 1, 'Acoustic Echoes & Symphony Live 2026', 'acoustic-echoes-symphony-2026', 'An electrifying evening featuring world-renowned acoustic instrumentalists and modern philharmonic orchestral arrangements.', 'Full concert featuring live orchestra with immersive sound systems and VIP stage access.', 'Grand Convention Hall', '100 Boulevard of the Arts', 'Metro City', DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY) + INTERVAL 4 HOUR, 'PUBLISHED', 1),
(2, 2, 2, 'International AI & Web 3.0 Summit', 'international-ai-web-summit-2026', 'Discover cutting-edge enterprise AI innovations, keynote panels from leading tech visionaries, and hands-on developer workshops.', 'Join over 2,000 developers and tech executives exploring the frontier of autonomous AI agents and enterprise cloud systems.', 'Silicon Center, Auditorium 4', '500 Tech Avenue', 'Metro City', DATE_ADD(NOW(), INTERVAL 45 DAY), DATE_ADD(NOW(), INTERVAL 46 DAY), 'PUBLISHED', 1),
(3, 2, 3, 'National Basketball Final Showdown', 'national-basketball-final-2026', 'Witness the grand culmination of the basketball season as top contenders battle for the championship trophy.', 'High-stakes championship game with courtside dining and halftime entertainment.', 'National Stadium', '1 Stadium Way', 'Metro City', DATE_ADD(NOW(), INTERVAL 60 DAY), DATE_ADD(NOW(), INTERVAL 60 DAY) + INTERVAL 3 HOUR, 'PUBLISHED', 1)
ON DUPLICATE KEY UPDATE `slug`=`slug`;

-- Seed 4: Sample Ticket Types
INSERT INTO `ticket_types` (`id`, `event_id`, `name`, `description`, `price`, `total_quantity`, `available_quantity`, `max_per_booking`)
VALUES
(1, 1, 'General Admission', 'Standard seating in main auditorium', 49.00, 300, 280, 5),
(2, 1, 'VIP Experience', 'Front row seating + exclusive backstage pass', 120.00, 50, 42, 2),
(3, 2, 'Attendee Pass', 'Access to keynotes and exhibition stalls', 79.00, 500, 480, 5),
(4, 2, 'Workshop & All-Access', 'Full access including developer code labs and luncheon', 199.00, 100, 95, 4),
(5, 3, 'Upper Deck', 'Upper tier stadium seating', 35.00, 800, 750, 8),
(6, 3, 'Courtside VIP', 'First-row courtside seat with exclusive lounge access', 250.00, 60, 48, 2)
ON DUPLICATE KEY UPDATE `name`=`name`;
