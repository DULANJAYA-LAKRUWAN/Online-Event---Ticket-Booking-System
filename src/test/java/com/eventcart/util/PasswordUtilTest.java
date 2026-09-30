package com.eventcart.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    @DisplayName("Password hashing produces valid BCrypt hash and verifies successfully")
    void testPasswordHashingAndVerification() {
        String rawPassword = "Admin@123";
        String hash = PasswordUtil.hashPassword(rawPassword);

        assertNotNull(hash, "Hash must not be null");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"), "Must be valid BCrypt format");
        assertTrue(PasswordUtil.checkPassword(rawPassword, hash), "Password check must succeed for matching raw password");
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hash), "Password check must fail for incorrect password");
    }

    @Test
    @DisplayName("Password hashing handles invalid input gracefully")
    void testPasswordHashingValidation() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(""));
        assertFalse(PasswordUtil.checkPassword(null, "someHash"));
        assertFalse(PasswordUtil.checkPassword("pwd", null));
        assertFalse(PasswordUtil.checkPassword("pwd", "invalidHash"));
    }
}
