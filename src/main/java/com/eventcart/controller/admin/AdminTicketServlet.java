package com.eventcart.controller.admin;

import com.eventcart.controller.BaseServlet;
import com.eventcart.entity.Event;
import com.eventcart.entity.TicketStatus;
import com.eventcart.entity.TicketType;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.EventService;
import com.eventcart.service.TicketTypeService;
import com.eventcart.service.impl.EventServiceImpl;
import com.eventcart.service.impl.TicketTypeServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing Ticket Types associated with an event.
 */
@WebServlet(name = "AdminTicketServlet", urlPatterns = {"/admin/events/tickets"})
public class AdminTicketServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final EventService eventService;
    private final TicketTypeService ticketTypeService;

    public AdminTicketServlet() {
        this(new EventServiceImpl(), new TicketTypeServiceImpl());
    }

    public AdminTicketServlet(EventService eventService, TicketTypeService ticketTypeService) {
        this.eventService = eventService;
        this.ticketTypeService = ticketTypeService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventIdParam = req.getParameter("eventId");
        if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
            setFlash(req, "error", "Event ID is required to manage tickets.");
            redirect(req, resp, "/admin/events");
            return;
        }

        try {
            Long eventId = Long.parseLong(eventIdParam.trim());
            Optional<Event> eventOpt = eventService.findById(eventId);
            if (eventOpt.isEmpty()) {
                setFlash(req, "error", "Event with ID " + eventId + " does not exist.");
                redirect(req, resp, "/admin/events");
                return;
            }

            Event event = eventOpt.get();
            List<TicketType> tickets = ticketTypeService.findByEvent(eventId);

            req.setAttribute("event", event);
            req.setAttribute("tickets", tickets);
            req.setAttribute("pageActive", "admin-events");
            forward(req, resp, "admin/tickets");

        } catch (NumberFormatException e) {
            setFlash(req, "error", "Invalid event ID format.");
            redirect(req, resp, "/admin/events");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventIdParam = req.getParameter("eventId");
        String action = getStringParameter(req, "action", "create");

        if (eventIdParam == null || eventIdParam.trim().isEmpty()) {
            setFlash(req, "error", "Event ID is required.");
            redirect(req, resp, "/admin/events");
            return;
        }

        Long eventId = Long.parseLong(eventIdParam.trim());

        try {
            switch (action) {
                case "create":
                    handleCreate(req, eventId);
                    break;
                case "edit":
                    handleEdit(req, eventId);
                    break;
                case "toggle":
                    handleToggle(req, eventId);
                    break;
                case "delete":
                    handleDelete(req, eventId);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown ticket action: " + action);
                    return;
            }
            redirect(req, resp, "/admin/events/tickets?eventId=" + eventId);

        } catch (ValidationException ve) {
            logger.warn("Validation error in ticket management: {}", ve.getMessage());
            setFlash(req, "error", ve.getMessage());
            redirect(req, resp, "/admin/events/tickets?eventId=" + eventId);
        } catch (Exception ex) {
            logger.error("Error managing tickets: {}", ex.getMessage(), ex);
            setFlash(req, "error", "Failed to update tickets: " + ex.getMessage());
            redirect(req, resp, "/admin/events/tickets?eventId=" + eventId);
        }
    }

    private void handleCreate(HttpServletRequest req, Long eventId) {
        String name = getStringParameter(req, "name", "");
        String description = getStringParameter(req, "description", "");
        BigDecimal price = new BigDecimal(getStringParameter(req, "price", "0.00"));
        int totalQuantity = getIntParameter(req, "totalQuantity", 0);
        String statusStr = getStringParameter(req, "status", "ACTIVE");
        TicketStatus status = "INACTIVE".equalsIgnoreCase(statusStr) ? TicketStatus.INACTIVE : TicketStatus.ACTIVE;

        ticketTypeService.createTicketType(eventId, name, description, price, totalQuantity, status);
        setFlash(req, "success", "Ticket tier '" + name + "' added successfully.");
    }

    private void handleEdit(HttpServletRequest req, Long eventId) {
        Long ticketId = Long.parseLong(req.getParameter("id"));
        String name = getStringParameter(req, "name", "");
        String description = getStringParameter(req, "description", "");
        BigDecimal price = new BigDecimal(getStringParameter(req, "price", "0.00"));
        int totalQuantity = getIntParameter(req, "totalQuantity", 0);
        int availableQuantity = getIntParameter(req, "availableQuantity", 0);
        String statusStr = getStringParameter(req, "status", "ACTIVE");
        TicketStatus status = "INACTIVE".equalsIgnoreCase(statusStr) ? TicketStatus.INACTIVE : TicketStatus.ACTIVE;

        ticketTypeService.updateTicketType(ticketId, name, description, price, totalQuantity, availableQuantity, status);
        setFlash(req, "success", "Ticket tier '" + name + "' updated successfully.");
    }

    private void handleToggle(HttpServletRequest req, Long eventId) {
        Long ticketId = Long.parseLong(req.getParameter("id"));
        TicketType updated = ticketTypeService.toggleStatus(ticketId);
        setFlash(req, "success", "Ticket status changed to " + updated.getStatus().getDisplayName());
    }

    private void handleDelete(HttpServletRequest req, Long eventId) {
        Long ticketId = Long.parseLong(req.getParameter("id"));
        ticketTypeService.deleteTicketType(ticketId);
        setFlash(req, "success", "Ticket tier deactivated.");
    }
}
