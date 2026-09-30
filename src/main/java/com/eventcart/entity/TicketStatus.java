package com.eventcart.entity;

/**
 * Status of a Ticket Type.
 */
public enum TicketStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
