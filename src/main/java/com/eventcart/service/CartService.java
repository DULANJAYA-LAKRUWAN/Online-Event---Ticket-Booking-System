package com.eventcart.service;

import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartItemDto;
import com.eventcart.dto.CartValidationResult;
import jakarta.servlet.http.HttpSession;

/**
 * Service interface governing session-based shopping cart business rules.
 */
public interface CartService {

    /**
     * Adds a ticket tier with requested quantity to the session cart.
     * Enforces event status, ticket status, per-line quantity limit, and current available stock.
     */
    CartItemDto addItem(HttpSession session, Long ticketTypeId, int quantity);

    /**
     * Updates the quantity of an existing item in the session cart.
     * Verifies that new quantity is within bounds and does not exceed available stock.
     */
    CartItemDto updateItemQuantity(HttpSession session, Long ticketTypeId, int quantity);

    /**
     * Removes a ticket tier from the session cart.
     */
    boolean removeItem(HttpSession session, Long ticketTypeId);

    /**
     * Clears all items from the session cart.
     */
    void clearCart(HttpSession session);

    /**
     * Retrieves the current session cart, initializing an empty cart if not yet present.
     */
    CartDto getCart(HttpSession session);

    /**
     * Returns total ticket count across all items in the session cart.
     */
    int getCartCount(HttpSession session);

    /**
     * Validates the entire cart against the current database state (stock, prices, active status).
     */
    CartValidationResult validateCart(CartDto cart);
}
