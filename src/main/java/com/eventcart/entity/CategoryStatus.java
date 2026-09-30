package com.eventcart.entity;

/**
 * Status of an Event category.
 */
public enum CategoryStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String displayName;

    CategoryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
