package com.eventcart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password utility ensuring all user passwords are securely hashed using BCrypt.
 * Plaintext passwords must NEVER be persisted or logged.
 */
public final class PasswordUtil {

    // BCrypt workload factor (log rounds, 10-12 is industry standard)
    private static final int BCRYPT_LOG_ROUNDS = 12;

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Hashes a raw password using BCrypt with a newly generated salt.
     *
     * @param plainPassword Raw password
     * @return 60-character BCrypt hash string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies a plain password against an existing BCrypt hash.
     *
     * @param plainPassword  Raw password provided by user
     * @param hashedPassword Stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }
}
