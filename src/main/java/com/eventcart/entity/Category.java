package com.eventcart.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Event category grouping events by theme (e.g., Music, Tech, Sports, Arts).
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_categories_slug", columnList = "slug", unique = true),
    @Index(name = "idx_categories_status", columnList = "status")
})
public class Category extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "icon_class", length = 50)
    private String iconClass = "bi-tag";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CategoryStatus status = CategoryStatus.ACTIVE;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();

    public Category() {
    }

    public Category(String name, String slug, String description, String iconClass, CategoryStatus status) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.iconClass = iconClass != null ? iconClass : "bi-tag";
        this.status = status != null ? status : CategoryStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == CategoryStatus.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public CategoryStatus getStatus() {
        return status;
    }

    public void setStatus(CategoryStatus status) {
        this.status = status;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
