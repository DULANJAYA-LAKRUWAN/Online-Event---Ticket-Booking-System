package com.eventcart.controller.pub;

import com.eventcart.controller.BaseServlet;
import com.eventcart.entity.Category;
import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;
import com.eventcart.service.CategoryService;
import com.eventcart.service.EventService;
import com.eventcart.service.impl.CategoryServiceImpl;
import com.eventcart.service.impl.EventServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Public controller for discovering and searching published events.
 */
@WebServlet(name = "PublicEventListServlet", urlPatterns = {"/events"})
public class PublicEventListServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final EventService eventService;
    private final CategoryService categoryService;

    public PublicEventListServlet() {
        this(new EventServiceImpl(), new CategoryServiceImpl());
    }

    public PublicEventListServlet(EventService eventService, CategoryService categoryService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        String categoryParam = req.getParameter("category");
        String location = req.getParameter("location");
        int page = getIntParameter(req, "page", 1);
        int pageSize = 9;

        Long categoryId = null;
        if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            try {
                categoryId = Long.parseLong(categoryParam.trim());
            } catch (NumberFormatException e) {
                // Try resolving by slug
                Optional<Category> catOpt = categoryService.findBySlug(categoryParam.trim());
                if (catOpt.isPresent()) {
                    categoryId = catOpt.get().getId();
                }
            }
        }

        // Only search PUBLISHED events
        List<Event> events = eventService.searchEvents(query, categoryId, location, EventStatus.PUBLISHED, page, pageSize);
        long totalEvents = eventService.countSearchEvents(query, categoryId, location, EventStatus.PUBLISHED);
        int totalPages = (int) Math.ceil((double) totalEvents / pageSize);

        List<Category> activeCategories = categoryService.findActiveCategories();

        req.setAttribute("events", events);
        req.setAttribute("categories", activeCategories);
        req.setAttribute("query", query);
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("selectedCategorySlug", categoryParam);
        req.setAttribute("location", location);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", Math.max(1, totalPages));
        req.setAttribute("totalEvents", totalEvents);
        req.setAttribute("pageActive", "events");
        req.setAttribute("pageTitle", "Explore Live Events & Tickets | EventCart");

        forward(req, resp, "public/events");
    }
}
