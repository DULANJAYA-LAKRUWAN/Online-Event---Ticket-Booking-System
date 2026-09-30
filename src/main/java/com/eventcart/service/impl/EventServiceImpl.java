package com.eventcart.service.impl;

import com.eventcart.dao.CategoryDao;
import com.eventcart.dao.EventDao;
import com.eventcart.dao.UserDao;
import com.eventcart.dao.impl.CategoryDaoImpl;
import com.eventcart.dao.impl.EventDaoImpl;
import com.eventcart.dao.impl.UserDaoImpl;
import com.eventcart.entity.Category;
import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;
import com.eventcart.entity.User;
import com.eventcart.exception.ResourceNotFoundException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.EventService;
import com.eventcart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service implementation for Event management, validation, and search.
 */
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    private final EventDao eventDao;
    private final CategoryDao categoryDao;
    private final UserDao userDao;

    public EventServiceImpl() {
        this(new EventDaoImpl(), new CategoryDaoImpl(), new UserDaoImpl());
    }

    public EventServiceImpl(EventDao eventDao, CategoryDao categoryDao, UserDao userDao) {
        this.eventDao = eventDao;
        this.categoryDao = categoryDao;
        this.userDao = userDao;
    }

    @Override
    public Event createEvent(Long categoryId, Long organizerId, String title, String description,
                             String venue, String location, LocalDate eventDate, LocalTime eventTime,
                             String bannerImage, boolean featured, EventStatus status) {
        logger.info("Creating event: '{}' in category ID: {}", title, categoryId);
        validateEventDetails(categoryId, title, description, venue, location, eventDate, eventTime);

        Category category = categoryDao.findById(categoryId)
                .orElseThrow(() -> new ValidationException("Selected category does not exist."));

        if (!category.isActive() && status == EventStatus.PUBLISHED) {
            throw new ValidationException("Cannot publish an event under an inactive category.");
        }

        User organizer = null;
        if (organizerId != null) {
            organizer = userDao.findById(organizerId).orElse(null);
        }

        String slug = generateUniqueSlug(title, null);

        Event event = new Event();
        event.setCategory(category);
        event.setOrganizer(organizer);
        event.setTitle(title.trim());
        event.setSlug(slug);
        event.setDescription(description.trim());
        event.setVenue(venue.trim());
        event.setLocation(location.trim());
        event.setEventDate(eventDate);
        event.setEventTime(eventTime);
        event.setBannerImage(ValidationUtil.clean(bannerImage));
        event.setFeatured(featured);
        event.setStatus(status != null ? status : EventStatus.DRAFT);

        Event saved = eventDao.save(event);
        logger.info("Event created successfully with ID: {}, Slug: {}", saved.getId(), saved.getSlug());
        return saved;
    }

    @Override
    public Event updateEvent(Long eventId, Long categoryId, String title, String description,
                             String venue, String location, LocalDate eventDate, LocalTime eventTime,
                             String bannerImage, boolean featured, EventStatus status) {
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        validateEventDetails(categoryId, title, description, venue, location, eventDate, eventTime);

        Category category = categoryDao.findById(categoryId)
                .orElseThrow(() -> new ValidationException("Selected category does not exist."));

        if (!category.isActive() && status == EventStatus.PUBLISHED) {
            throw new ValidationException("Cannot publish an event under an inactive category.");
        }

        event.setCategory(category);
        event.setTitle(title.trim());
        event.setSlug(generateUniqueSlug(title, eventId));
        event.setDescription(description.trim());
        event.setVenue(venue.trim());
        event.setLocation(location.trim());
        event.setEventDate(eventDate);
        event.setEventTime(eventTime);
        if (bannerImage != null && !bannerImage.trim().isEmpty()) {
            event.setBannerImage(bannerImage.trim());
        }
        event.setFeatured(featured);
        if (status != null) {
            event.setStatus(status);
        }

        Event updated = eventDao.update(event);
        logger.info("Event id {} updated successfully.", eventId);
        return updated;
    }

    @Override
    public void deleteEvent(Long eventId) {
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        // Soft-deactivate if it has bookings/ticket history, or mark CANCELLED
        event.setStatus(EventStatus.CANCELLED);
        eventDao.update(event);
        logger.info("Event id {} marked as CANCELLED.", eventId);
    }

    @Override
    public Event updateStatus(Long eventId, EventStatus newStatus) {
        if (newStatus == null) {
            throw new ValidationException("Event status cannot be null.");
        }
        Event event = eventDao.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        if (newStatus == EventStatus.PUBLISHED && !event.getCategory().isActive()) {
            throw new ValidationException("Cannot publish event under an inactive category.");
        }

        event.setStatus(newStatus);
        Event updated = eventDao.update(event);
        logger.info("Event id {} status transitioned to {}", eventId, newStatus);
        return updated;
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventDao.findById(id);
    }

    @Override
    public Optional<Event> findBySlug(String slug) {
        return eventDao.findBySlug(slug);
    }

    @Override
    public List<Event> findAll() {
        return eventDao.findAll();
    }

    @Override
    public List<Event> findPublishedEvents() {
        return eventDao.findPublished();
    }

    @Override
    public List<Event> findFeaturedEvents(int limit) {
        return eventDao.findFeatured(limit);
    }

    @Override
    public List<Event> findByCategory(Long categoryId) {
        return eventDao.findByCategory(categoryId);
    }

    @Override
    public List<Event> searchEvents(String query, Long categoryId, String location, EventStatus status, int page, int pageSize) {
        return eventDao.searchEvents(query, categoryId, location, status, page, pageSize);
    }

    @Override
    public long countSearchEvents(String query, Long categoryId, String location, EventStatus status) {
        return eventDao.countSearchEvents(query, categoryId, location, status);
    }

    @Override
    public String generateUniqueSlug(String title, Long currentId) {
        String base = toSlug(title);
        String candidate = base;
        int counter = 1;

        while (true) {
            Optional<Event> found = eventDao.findBySlug(candidate);
            if (found.isEmpty() || (currentId != null && found.get().getId().equals(currentId))) {
                return candidate;
            }
            candidate = base + "-" + (++counter);
        }
    }

    private void validateEventDetails(Long categoryId, String title, String description,
                                      String venue, String location, LocalDate eventDate, LocalTime eventTime) {
        if (categoryId == null || categoryId <= 0) {
            throw new ValidationException("A valid event category must be selected.");
        }
        if (!ValidationUtil.isNotBlank(title) || title.trim().length() < 3 || title.trim().length() > 150) {
            throw new ValidationException("Event title must be between 3 and 150 characters.");
        }
        if (!ValidationUtil.isNotBlank(description)) {
            throw new ValidationException("Event description is required.");
        }
        if (!ValidationUtil.isNotBlank(venue)) {
            throw new ValidationException("Venue name is required.");
        }
        if (!ValidationUtil.isNotBlank(location)) {
            throw new ValidationException("City / location is required.");
        }
        if (eventDate == null) {
            throw new ValidationException("Event date is required.");
        }
        if (eventTime == null) {
            throw new ValidationException("Event start time is required.");
        }
    }

    private static String toSlug(String input) {
        if (input == null) return "event";
        String noWhiteSpace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
        return slug.isEmpty() ? "event" : slug;
    }
}
