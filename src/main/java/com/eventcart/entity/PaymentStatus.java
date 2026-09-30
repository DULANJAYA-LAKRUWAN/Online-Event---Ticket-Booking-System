package com.eventcart.entity;

/**
 * Lifecycle status of a Transaction Payment.
 */
public enum PaymentStatus {
    INITIATED("Initiated"),
    PENDING("Pending"),
    SUCCESS("Success"),
    FAILED("Failed");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
