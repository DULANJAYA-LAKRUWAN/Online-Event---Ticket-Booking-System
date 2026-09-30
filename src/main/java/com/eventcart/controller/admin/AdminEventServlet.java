package com.eventcart.controller.admin;

import com.eventcart.controller.BaseServlet;
import com.eventcart.entity.Event;
import com.eventcart.entity.EventStatus;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.EventService;
import com.eventcart.service.impl.EventServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for listing and managing Events in the Admin portal.
 */
@WebServlet(name = "AdminEventServlet", urlPatterns = {"/admin/events"})
public class AdminEventServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final EventService eventService;

    public AdminEventServlet() {
        this(new EventServiceImpl());
    }

    public AdminEventServlet(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Event> events = eventService.findAll();
        req.setAttribute("events", events);
        req.setAttribute("pageActive", "admin-events");
        forward(req, resp, "admin/events");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = getStringParameter(req, "action", "status");
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))
                || (req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json"));

        try {
            switch (action) {
                case "status":
                    handleStatusChange(req, resp, isAjax);
                    break;
                case "delete":
                    handleDelete(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown event action: " + action);
            }
        } catch (ValidationException ve) {
            logger.warn("Validation error in AdminEventServlet: {}", ve.getMessage());
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, ve.getMessage());
            } else {
                setFlash(req, "error", ve.getMessage());
                redirect(req, resp, "/admin/events");
            }
        } catch (Exception ex) {
            logger.error("Error processing event action: {}", ex.getMessage(), ex);
            if (isAjax) {
                sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + ex.getMessage());
            } else {
                setFlash(req, "error", "An error occurred: " + ex.getMessage());
                redirect(req, resp, "/admin/events");
            }
        }
    }

    private void handleStatusChange(HttpServletRequest req, HttpServletResponse resp, boolean isAjax) throws IOException {
        Long eventId = Long.parseLong(req.getParameter("id"));
        String newStatusStr = getStringParameter(req, "status", "DRAFT");
        EventStatus newStatus = EventStatus.valueOf(newStatusStr.toUpperCase());

        Event updated = eventService.updateStatus(eventId, newStatus);

        if (isAjax) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", updated.getId());
            data.put("status", updated.getStatus().name());
            data.put("statusDisplay", updated.getStatus().getDisplayName());
            sendJsonOk(resp, "Event status updated to " + updated.getStatus().getDisplayName(), data);
        } else {
            setFlash(req, "success", "Event status updated to " + updated.getStatus().getDisplayName());
            redirect(req, resp, "/admin/events");
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long eventId = Long.parseLong(req.getParameter("id"));
        eventService.deleteEvent(eventId);

        setFlash(req, "success", "Event has been marked as cancelled.");
        redirect(req, resp, "/admin/events");
    }
}
