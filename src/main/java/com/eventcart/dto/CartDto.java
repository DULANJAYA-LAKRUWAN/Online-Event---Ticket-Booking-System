package com.eventcart.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object representing the customer's session shopping cart.
 */
public class CartDto implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SESSION_KEY = "EVENTCART_SESSION_CART";
    public static final int MAX_QUANTITY_PER_LINE = 10;

    private final Map<Long, CartItemDto> items = new LinkedHashMap<>();

    public CartDto() {
    }

    public synchronized void addItem(CartItemDto item) {
        if (item == null || item.getTicketTypeId() == null) return;
        Long ticketId = item.getTicketTypeId();
        if (items.containsKey(ticketId)) {
            CartItemDto existing = items.get(ticketId);
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            existing.setUnitPrice(item.getUnitPrice()); // Keep price fresh
            existing.setAvailableQuantity(item.getAvailableQuantity());
        } else {
            items.put(ticketId, item);
        }
    }

    public synchronized void updateQuantity(Long ticketTypeId, int quantity) {
        if (ticketTypeId == null) return;
        CartItemDto existing = items.get(ticketTypeId);
        if (existing != null) {
            if (quantity <= 0) {
                items.remove(ticketTypeId);
            } else {
                existing.setQuantity(quantity);
            }
        }
    }

    public synchronized boolean removeItem(Long ticketTypeId) {
        if (ticketTypeId == null) return false;
        return items.remove(ticketTypeId) != null;
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized int getTotalQuantity() {
        int total = 0;
        for (CartItemDto item : items.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    public synchronized BigDecimal getTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDto item : items.values()) {
            if (item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }
        return total;
    }

    public synchronized int getItemCount() {
        return items.size();
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    public synchronized Collection<CartItemDto> getItems() {
        return new ArrayList<>(items.values());
    }

    public synchronized List<CartItemDto> getItemsList() {
        return new ArrayList<>(items.values());
    }

    public synchronized CartItemDto getItem(Long ticketTypeId) {
        return items.get(ticketTypeId);
    }
}
