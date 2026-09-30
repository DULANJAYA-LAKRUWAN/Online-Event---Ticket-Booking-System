package com.eventcart.controller;

import com.eventcart.entity.Category;
import com.eventcart.entity.Event;
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

/**
 * Controller for application landing and home page.
 * Loads live featured events and active categories from Hibernate services.
 */
@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final EventService eventService;
    private final CategoryService categoryService;

    public HomeController() {
        this(new EventServiceImpl(), new CategoryServiceImpl());
    }

    public HomeController(EventService eventService, CategoryService categoryService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Serving Home page with live data.");

        try {
            List<Event> featuredEvents = eventService.findFeaturedEvents(6);
            List<Category> categories = categoryService.findActiveCategories();

            req.setAttribute("featuredEvents", featuredEvents);
            req.setAttribute("categories", categories);
        } catch (Exception e) {
            logger.warn("Could not load home page dynamic events (database may not be seeded yet): {}", e.getMessage());
        }

        req.setAttribute("pageTitle", "EventCart — Discover. Book. Experience.");
        req.setAttribute("pageActive", "home");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
