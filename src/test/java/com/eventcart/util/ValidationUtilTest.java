package com.eventcart.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @Test
    @DisplayName("Email validator accurately accepts valid and rejects malformed emails")
    void testEmailValidation() {
        assertTrue(ValidationUtil.isValidEmail("john.doe@example.com"));
        assertTrue(ValidationUtil.isValidEmail("admin@eventcart.lk"));
        assertFalse(ValidationUtil.isValidEmail("plainaddress"));
        assertFalse(ValidationUtil.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtil.isValidEmail("username@.com"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    @DisplayName("HTML escaper mitigates cross-site scripting inputs")
    void testHtmlEscaping() {
        String unsafe = "<script>alert('xss');</script>";
        String safe = ValidationUtil.escapeHtml(unsafe);
        assertFalse(safe.contains("<"));
        assertFalse(safe.contains(">"));
        assertTrue(safe.contains("&lt;script&gt;"));
    }
}
