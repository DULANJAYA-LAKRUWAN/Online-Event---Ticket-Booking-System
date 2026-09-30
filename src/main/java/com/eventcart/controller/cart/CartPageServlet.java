package com.eventcart.controller.cart;

import com.eventcart.controller.BaseServlet;
import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartValidationResult;
import com.eventcart.service.CartService;
import com.eventcart.service.impl.CartServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller serving the customer's full shopping cart page.
 */
@WebServlet(name = "CartPageServlet", urlPatterns = {"/cart"})
public class CartPageServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private CartService cartService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.cartService = new CartServiceImpl();
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        CartDto cart = cartService.getCart(session);

        // Run validation against database to surface inventory/price updates
        CartValidationResult validationResult = cartService.validateCart(cart);

        req.setAttribute("cart", cart);
        req.setAttribute("cartValidation", validationResult);
        req.setAttribute("pageActive", "cart");

        forward(req, resp, "cart/cart");
    }
}
