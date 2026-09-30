package com.eventcart.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data Transfer Object representing an individual line item in the shopping cart.
 */
public class CartItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long ticketTypeId;
    private Long eventId;
    private String eventTitle;
    private String ticketTypeName;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private int quantity;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private int availableQuantity;
    private String bannerImage;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String venue;

    public CartItemDto() {
    }

    public CartItemDto(Long ticketTypeId, Long eventId, String eventTitle, String ticketTypeName,
                       BigDecimal unitPrice, int quantity, int availableQuantity,
                       String bannerImage, LocalDate eventDate, LocalTime eventTime, String venue) {
        this.ticketTypeId = ticketTypeId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.ticketTypeName = ticketTypeName;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.bannerImage = bannerImage;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        recalculateSubtotal();
    }

    public void recalculateSubtotal() {
        if (this.unitPrice != null && this.quantity > 0) {
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    public Long getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Long ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getTicketTypeName() {
        return ticketTypeName;
    }

    public void setTicketTypeName(String ticketTypeName) {
        this.ticketTypeName = ticketTypeName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
