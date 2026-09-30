package com.eventcart.util;

import java.util.regex.Pattern;

/**
 * Validation and sanitization utility for incoming user inputs.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+0-9\\-\\s()]{7,25}$"
    );

    private ValidationUtil() {
        // Utility class
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return true; // Optional field
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isNotBlank(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isStrongPassword(String password) {
        // Minimum 6 characters for academic demonstration
        return password != null && password.length() >= 6;
    }

    /**
     * Basic HTML escaping to mitigate XSS in user rendered content.
     */
    public static String escapeHtml(String input) {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Trims and returns null if string is empty.
     */
    public static String clean(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
