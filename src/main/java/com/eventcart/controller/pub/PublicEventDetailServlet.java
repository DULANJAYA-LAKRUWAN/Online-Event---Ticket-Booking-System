package com.eventcart.controller.pub;

import com.eventcart.controller.BaseServlet;
import com.eventcart.entity.Event;
import com.eventcart.entity.TicketType;
import com.eventcart.service.EventService;
import com.eventcart.service.TicketTypeService;
import com.eventcart.service.impl.EventServiceImpl;
import com.eventcart.service.impl.TicketTypeServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Public controller for viewing detailed event information and available ticket tiers.
 */
@WebServlet(name = "PublicEventDetailServlet", urlPatterns = {"/event"})
public class PublicEventDetailServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final EventService eventService;
    private final TicketTypeService ticketTypeService;

    public PublicEventDetailServlet() {
        this(new EventServiceImpl(), new TicketTypeServiceImpl());
    }

    public PublicEventDetailServlet(EventService eventService, TicketTypeService ticketTypeService) {
        this.eventService = eventService;
        this.ticketTypeService = ticketTypeService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String slug = req.getParameter("slug");
        String idParam = req.getParameter("id");

        Optional<Event> eventOpt = Optional.empty();

        if (slug != null && !slug.trim().isEmpty()) {
            eventOpt = eventService.findBySlug(slug.trim());
        } else if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(idParam.trim());
                eventOpt = eventService.findById(id);
            } catch (NumberFormatException e) {
                // Ignore invalid id format
            }
        }

        if (eventOpt.isEmpty() || !eventOpt.get().isPublished()) {
            req.setAttribute("errorMessage", "The requested event could not be found or is not currently active.");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            forward(req, resp, "error/404");
            return;
        }

        Event event = eventOpt.get();
        List<TicketType> tickets = ticketTypeService.findActiveByEvent(event.getId());

        req.setAttribute("event", event);
        req.setAttribute("tickets", tickets);
        req.setAttribute("pageTitle", event.getTitle() + " | EventCart");
        req.setAttribute("pageActive", "events");

        forward(req, resp, "public/event-details");
    }
}
