package com.eventcart.controller.admin;

import com.eventcart.controller.BaseServlet;
import com.eventcart.entity.Category;
import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;
import com.eventcart.entity.User;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.CategoryService;
import com.eventcart.service.EventService;
import com.eventcart.service.impl.CategoryServiceImpl;
import com.eventcart.service.impl.EventServiceImpl;
import com.eventcart.util.FileUploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller for creating and editing events, handling multipart banner image uploads.
 */
@WebServlet(name = "AdminEventFormServlet", urlPatterns = {"/admin/events/form"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 5 * 1024 * 1024,       // 5 MB
        maxRequestSize = 20 * 1024 * 1024    // 20 MB
)
public class AdminEventFormServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final EventService eventService;
    private final CategoryService categoryService;

    public AdminEventFormServlet() {
        this(new EventServiceImpl(), new CategoryServiceImpl());
    }

    public AdminEventFormServlet(EventService eventService, CategoryService categoryService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                Long eventId = Long.parseLong(idParam.trim());
                Optional<Event> eventOpt = eventService.findById(eventId);
                if (eventOpt.isPresent()) {
                    req.setAttribute("event", eventOpt.get());
                } else {
                    setFlash(req, "error", "Event with ID " + eventId + " was not found.");
                    redirect(req, resp, "/admin/events");
                    return;
                }
            } catch (NumberFormatException e) {
                setFlash(req, "error", "Invalid event ID.");
                redirect(req, resp, "/admin/events");
                return;
            }
        }

        List<Category> categories = categoryService.findActiveCategories();
        req.setAttribute("categories", categories);
        req.setAttribute("pageActive", "admin-events");
        forward(req, resp, "admin/event-form");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        Long eventId = (idParam != null && !idParam.trim().isEmpty()) ? Long.parseLong(idParam.trim()) : null;

        Long categoryId = Long.parseLong(req.getParameter("categoryId"));
        String title = getStringParameter(req, "title", "");
        String description = getStringParameter(req, "description", "");
        String venue = getStringParameter(req, "venue", "");
        String location = getStringParameter(req, "location", "");
        String dateStr = getStringParameter(req, "eventDate", "");
        String timeStr = getStringParameter(req, "eventTime", "");
        boolean featured = "true".equalsIgnoreCase(req.getParameter("featured")) || "on".equalsIgnoreCase(req.getParameter("featured"));
        String statusStr = getStringParameter(req, "status", "DRAFT");
        EventStatus status = EventStatus.valueOf(statusStr.toUpperCase());

        try {
            LocalDate eventDate = LocalDate.parse(dateStr);
            LocalTime eventTime = LocalTime.parse(timeStr);

            // Handle Multipart banner image upload
            String bannerPath = null;
            Part filePart = req.getPart("bannerImage");
            if (filePart != null && filePart.getSize() > 0) {
                String appRealPath = getServletContext().getRealPath("");
                bannerPath = FileUploadUtil.uploadEventBanner(filePart, appRealPath);
            }

            if (eventId == null) {
                // Create
                User authUser = getAuthenticatedUser(req);
                Long organizerId = (authUser != null) ? authUser.getId() : null;
                Event created = eventService.createEvent(categoryId, organizerId, title, description,
                        venue, location, eventDate, eventTime, bannerPath, featured, status);
                setFlash(req, "success", "Event '" + created.getTitle() + "' created successfully.");
            } else {
                // Update
                Event updated = eventService.updateEvent(eventId, categoryId, title, description,
                        venue, location, eventDate, eventTime, bannerPath, featured, status);
                setFlash(req, "success", "Event '" + updated.getTitle() + "' updated successfully.");
            }

            redirect(req, resp, "/admin/events");

        } catch (ValidationException ve) {
            logger.warn("Validation failure in event form: {}", ve.getMessage());
            setFlash(req, "error", ve.getMessage());
            req.setAttribute("categories", categoryService.findActiveCategories());
            forward(req, resp, "admin/event-form");
        } catch (Exception ex) {
            logger.error("Error saving event: {}", ex.getMessage(), ex);
            setFlash(req, "error", "Failed to save event: " + ex.getMessage());
            req.setAttribute("categories", categoryService.findActiveCategories());
            forward(req, resp, "admin/event-form");
        }
    }
}
