package com.eventcart.service;

import com.eventcart.dao.TicketTypeDao;
import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartItemDto;
import com.eventcart.dto.CartValidationResult;
import com.eventcart.entity.*;
import com.eventcart.exception.InventoryConflictException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.impl.CartServiceImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private MockTicketTypeDao mockTicketTypeDao;
    private CartService cartService;
    private MockHttpSession session;

    private Event publishedEvent;
    private Event draftEvent;
    private TicketType activeTicket;
    private TicketType inactiveTicket;
    private TicketType soldOutTicket;

    @BeforeEach
    void setUp() {
        mockTicketTypeDao = new MockTicketTypeDao();
        cartService = new CartServiceImpl(mockTicketTypeDao);
        session = new MockHttpSession();

        Category cat = new Category("Music", "music", "Live Concerts", "bi-music", CategoryStatus.ACTIVE);
        cat.setId(1L);

        publishedEvent = new Event();
        publishedEvent.setId(100L);
        publishedEvent.setTitle("Grand Symphony");
        publishedEvent.setCategory(cat);
        publishedEvent.setVenue("Royal Theatre");
        publishedEvent.setLocation("London, UK");
        publishedEvent.setEventDate(LocalDate.of(2026, 12, 1));
        publishedEvent.setEventTime(LocalTime.of(19, 30));
        publishedEvent.setStatus(EventStatus.PUBLISHED);

        draftEvent = new Event();
        draftEvent.setId(200L);
        draftEvent.setTitle("Secret Rehearsal");
        draftEvent.setCategory(cat);
        draftEvent.setStatus(EventStatus.DRAFT);

        activeTicket = new TicketType(publishedEvent, "VIP Seat", "Front row access", new BigDecimal("120.00"), 50, 20, TicketStatus.ACTIVE);
        activeTicket.setId(1L);
        mockTicketTypeDao.save(activeTicket);

        inactiveTicket = new TicketType(publishedEvent, "Early Bird", "Discounted", new BigDecimal("60.00"), 30, 10, TicketStatus.INACTIVE);
        inactiveTicket.setId(2L);
        mockTicketTypeDao.save(inactiveTicket);

        soldOutTicket = new TicketType(publishedEvent, "Backstage Pass", "VIP Meet", new BigDecimal("250.00"), 10, 0, TicketStatus.ACTIVE);
        soldOutTicket.setId(3L);
        mockTicketTypeDao.save(soldOutTicket);
    }

    @Test
    @DisplayName("Successfully add ticket to cart with server-side price")
    void testAddItemSuccess() {
        CartItemDto item = cartService.addItem(session, activeTicket.getId(), 2);

        assertNotNull(item);
        assertEquals(1L, item.getTicketTypeId());
        assertEquals("VIP Seat", item.getTicketTypeName());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("120.00"), item.getUnitPrice());
        assertEquals(new BigDecimal("240.00"), item.getSubtotal());

        assertEquals(2, cartService.getCartCount(session));
        CartDto cart = cartService.getCart(session);
        assertEquals(new BigDecimal("240.00"), cart.getTotalAmount());
    }

    @Test
    @DisplayName("Adding duplicate ticket merges quantity and recalculates subtotal")
    void testAddDuplicateItemMergesQuantity() {
        cartService.addItem(session, activeTicket.getId(), 2);
        CartItemDto updated = cartService.addItem(session, activeTicket.getId(), 3);

        assertEquals(5, updated.getQuantity());
        assertEquals(new BigDecimal("600.00"), updated.getSubtotal());
        assertEquals(5, cartService.getCartCount(session));
    }

    @Test
    @DisplayName("Reject adding ticket with invalid non-positive quantity")
    void testAddItemInvalidQuantity() {
        assertThrows(ValidationException.class, () ->
                cartService.addItem(session, activeTicket.getId(), 0));
        assertThrows(ValidationException.class, () ->
                cartService.addItem(session, activeTicket.getId(), -5));
    }

    @Test
    @DisplayName("Reject adding ticket for unpublished event")
    void testAddItemUnpublishedEvent() {
        TicketType draftTicket = new TicketType(draftEvent, "Rehearsal Pass", "", new BigDecimal("40.00"), 20, 20, TicketStatus.ACTIVE);
        draftTicket.setId(4L);
        mockTicketTypeDao.save(draftTicket);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                cartService.addItem(session, draftTicket.getId(), 1));
        assertTrue(ex.getMessage().contains("published events"));
    }

    @Test
    @DisplayName("Reject adding inactive ticket tier")
    void testAddItemInactiveTicket() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                cartService.addItem(session, inactiveTicket.getId(), 1));
        assertTrue(ex.getMessage().contains("inactive"));
    }

    @Test
    @DisplayName("Reject adding sold-out ticket with 409 Inventory Conflict")
    void testAddItemSoldOut() {
        InventoryConflictException ex = assertThrows(InventoryConflictException.class, () ->
                cartService.addItem(session, soldOutTicket.getId(), 1));
        assertTrue(ex.getMessage().contains("sold out"));
    }

    @Test
    @DisplayName("Reject adding quantity greater than available stock")
    void testAddItemExceedsAvailableStock() {
        activeTicket.setAvailableQuantity(4);
        InventoryConflictException ex = assertThrows(InventoryConflictException.class, () ->
                cartService.addItem(session, activeTicket.getId(), 6));
        assertTrue(ex.getMessage().contains("available"));
    }

    @Test
    @DisplayName("Reject adding total quantity exceeding per-line maximum of 10")
    void testAddItemExceedsPerLineLimit() {
        cartService.addItem(session, activeTicket.getId(), 8);
        ValidationException ex = assertThrows(ValidationException.class, () ->
                cartService.addItem(session, activeTicket.getId(), 3)); // 8 + 3 = 11 > 10
        assertTrue(ex.getMessage().contains("more than 10"));
    }

    @Test
    @DisplayName("Update item quantity in cart")
    void testUpdateItemQuantity() {
        cartService.addItem(session, activeTicket.getId(), 2);
        CartItemDto updated = cartService.updateItemQuantity(session, activeTicket.getId(), 4);

        assertNotNull(updated);
        assertEquals(4, updated.getQuantity());
        assertEquals(new BigDecimal("480.00"), updated.getSubtotal());
        assertEquals(4, cartService.getCartCount(session));
    }

    @Test
    @DisplayName("Update item quantity to 0 removes item from cart")
    void testUpdateQuantityToZeroRemovesItem() {
        cartService.addItem(session, activeTicket.getId(), 2);
        CartItemDto result = cartService.updateItemQuantity(session, activeTicket.getId(), 0);

        assertNull(result);
        assertEquals(0, cartService.getCartCount(session));
        assertTrue(cartService.getCart(session).isEmpty());
    }

    @Test
    @DisplayName("Remove item from cart")
    void testRemoveItem() {
        cartService.addItem(session, activeTicket.getId(), 3);
        boolean removed = cartService.removeItem(session, activeTicket.getId());

        assertTrue(removed);
        assertEquals(0, cartService.getCartCount(session));
    }

    @Test
    @DisplayName("Clear all items from cart")
    void testClearCart() {
        cartService.addItem(session, activeTicket.getId(), 2);
        cartService.clearCart(session);

        assertEquals(0, cartService.getCartCount(session));
        assertTrue(cartService.getCart(session).isEmpty());
    }

    @Test
    @DisplayName("Validate cart reports stock decrease and price changes")
    void testValidateCartWithStockAndPriceChanges() {
        cartService.addItem(session, activeTicket.getId(), 5);

        // Simulate background inventory purchase by another user: available dropped to 3
        activeTicket.setAvailableQuantity(3);
        activeTicket.setPrice(new BigDecimal("130.00")); // price increased

        CartDto cart = cartService.getCart(session);
        CartValidationResult validation = cartService.validateCart(cart);

        assertFalse(validation.isValid());
        assertEquals(1, validation.getErrorMessages().size());
        assertTrue(validation.getErrorMessages().get(0).contains("Only 3 tickets available"));
        assertEquals(1, validation.getWarningMessages().size());
        assertTrue(validation.getWarningMessages().get(0).contains("Price for 'VIP Seat' updated"));
    }

    // -------------------------------------------------------------------------
    // Mock Helpers
    // -------------------------------------------------------------------------

    private static class MockTicketTypeDao implements TicketTypeDao {
        private final Map<Long, TicketType> store = new HashMap<>();

        @Override
        public Optional<TicketType> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<TicketType> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public TicketType save(TicketType entity) {
            if (entity.getId() == null) {
                entity.setId((long) (store.size() + 1));
            }
            store.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public TicketType update(TicketType entity) {
            store.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public void delete(TicketType entity) {
            if (entity != null) store.remove(entity.getId());
        }

        @Override
        public void deleteById(Long id) {
            store.remove(id);
        }

        @Override
        public List<TicketType> findPaginated(int page, int pageSize) {
            return new ArrayList<>(store.values());
        }

        @Override
        public long count() {
            return store.size();
        }

        @Override
        public List<TicketType> findByEvent(Long eventId) {
            List<TicketType> list = new ArrayList<>();
            for (TicketType t : store.values()) {
                if (t.getEvent() != null && Objects.equals(t.getEvent().getId(), eventId)) {
                    list.add(t);
                }
            }
            return list;
        }

        @Override
        public List<TicketType> findActiveByEvent(Long eventId) {
            List<TicketType> list = new ArrayList<>();
            for (TicketType t : store.values()) {
                if (t.getEvent() != null && Objects.equals(t.getEvent().getId(), eventId)
                        && t.getStatus() == TicketStatus.ACTIVE) {
                    list.add(t);
                }
            }
            return list;
        }

        @Override
        public boolean updateAvailableQuantity(Long ticketTypeId, int deltaQuantity) {
            TicketType t = store.get(ticketTypeId);
            if (t == null) return false;
            int newQty = t.getAvailableQuantity() + deltaQuantity;
            if (newQty < 0 || newQty > t.getTotalQuantity()) return false;
            t.setAvailableQuantity(newQty);
            return true;
        }

        @Override
        public Optional<TicketType> findByIdWithLock(Session session, Long ticketTypeId) {
            return Optional.ofNullable(store.get(ticketTypeId));
        }
    }

    private static class MockHttpSession implements HttpSession {
        private final Map<String, Object> attributes = new HashMap<>();

        @Override public Object getAttribute(String s) { return attributes.get(s); }
        @Override public void setAttribute(String s, Object o) { attributes.put(s, o); }
        @Override public void removeAttribute(String s) { attributes.remove(s); }
        @Override public Enumeration<String> getAttributeNames() { return Collections.enumeration(attributes.keySet()); }
        @Override public void invalidate() { attributes.clear(); }
        @Override public boolean isNew() { return false; }
        @Override public long getCreationTime() { return 0; }
        @Override public String getId() { return "mock-session-id"; }
        @Override public long getLastAccessedTime() { return 0; }
        @Override public ServletContext getServletContext() { return null; }
        @Override public void setMaxInactiveInterval(int i) { }
        @Override public int getMaxInactiveInterval() { return 1800; }
    }
}
