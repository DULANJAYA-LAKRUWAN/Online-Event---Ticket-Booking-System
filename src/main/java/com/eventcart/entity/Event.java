package com.eventcart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Event entity representing published, draft, or completed event listings.
 */
@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_events_slug", columnList = "slug", unique = true),
    @Index(name = "idx_events_category", columnList = "category_id"),
    @Index(name = "idx_events_date", columnList = "event_date"),
    @Index(name = "idx_events_status", columnList = "status"),
    @Index(name = "idx_events_featured", columnList = "featured")
})
public class Event extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = true)
    private User organizer;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 220)
    private String slug;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "venue", nullable = false, length = 150)
    private String venue;

    @Column(name = "location", nullable = false, length = 150)
    private String location;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_time", nullable = false)
    private LocalTime eventTime;

    @Column(name = "banner_image", length = 255)
    private String bannerImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus status = EventStatus.DRAFT;

    @Column(name = "featured", nullable = false)
    private boolean featured = false;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("price ASC")
    private List<TicketType> ticketTypes = new ArrayList<>();

    public Event() {
    }

    public boolean isPublished() {
        return this.status == EventStatus.PUBLISHED;
    }

    /**
     * Calculates starting price from the lowest active ticket type.
     */
    public BigDecimal getStartingPrice() {
        if (ticketTypes == null || ticketTypes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return ticketTypes.stream()
                .filter(t -> t.getStatus() == TicketStatus.ACTIVE)
                .map(TicketType::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Calculates total tickets available for booking across all active ticket tiers.
     */
    public int getTotalAvailableTickets() {
        if (ticketTypes == null || ticketTypes.isEmpty()) {
            return 0;
        }
        return ticketTypes.stream()
                .filter(t -> t.getStatus() == TicketStatus.ACTIVE)
                .mapToInt(TicketType::getAvailableQuantity)
                .sum();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public List<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(List<TicketType> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }
}
