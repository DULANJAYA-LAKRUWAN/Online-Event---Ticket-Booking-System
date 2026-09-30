package com.eventcart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Ticket tier for an event (e.g. Early Bird, Regular, VIP, Premium).
 */
@Entity
@Table(name = "ticket_types", indexes = {
    @Index(name = "idx_ticket_types_event", columnList = "event_id"),
    @Index(name = "idx_ticket_types_status", columnList = "status")
})
public class TicketType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 300)
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status = TicketStatus.ACTIVE;

    public TicketType() {
    }

    public TicketType(Event event, String name, String description, BigDecimal price, int totalQuantity, int availableQuantity, TicketStatus status) {
        this.event = event;
        this.name = name;
        this.description = description;
        this.price = price != null ? price : BigDecimal.ZERO;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.status = status != null ? status : TicketStatus.ACTIVE;
    }

    public boolean isAvailable() {
        return this.status == TicketStatus.ACTIVE && this.availableQuantity > 0;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
