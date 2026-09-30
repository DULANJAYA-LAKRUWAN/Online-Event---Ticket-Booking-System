package com.eventcart.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controller for application landing and home page.
 */
@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("Serving Home page.");
        req.setAttribute("pageTitle", "Discover Events & Book Tickets");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
