package com.eventcart.service;

import com.eventcart.dao.BookingDao;
import com.eventcart.dao.TicketTypeDao;
import com.eventcart.dto.CartDto;
import com.eventcart.dto.CartItemDto;
import com.eventcart.entity.*;
import com.eventcart.exception.InventoryConflictException;
import com.eventcart.exception.ValidationException;
import com.eventcart.service.impl.BookingServiceImpl;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    private MockBookingDao mockBookingDao;
    private MockTicketTypeDao mockTicketTypeDao;
    private BookingServiceImpl bookingService;

    private User sampleUser;
    private Event sampleEvent;
    private TicketType availableTicket;
    private TicketType limitedTicket;
    private TicketType soldOutTicket;

    @BeforeEach
    void setUp() {
        mockBookingDao = new MockBookingDao();
        mockTicketTypeDao = new MockTicketTypeDao();
        bookingService = new BookingServiceImpl(mockBookingDao, mockTicketTypeDao);

        sampleUser = new User("alice@example.com", "hash", "Alice Smith", Role.CUSTOMER);
        sampleUser.setPhoneNumber("0771234567");
        sampleUser.setId(10L);

        Category cat = new Category("Concerts", "concerts", "Live musical events", "bi-music-note", CategoryStatus.ACTIVE);
        cat.setId(1L);

        sampleEvent = new Event();
        sampleEvent.setId(100L);
        sampleEvent.setTitle("Neon Beats Festival");
        sampleEvent.setCategory(cat);
        sampleEvent.setVenue("Grand Arena");
        sampleEvent.setLocation("Colombo, Sri Lanka");
        sampleEvent.setEventDate(LocalDate.of(2026, 11, 20));
        sampleEvent.setEventTime(LocalTime.of(18, 0));
        sampleEvent.setStatus(EventStatus.PUBLISHED);

        availableTicket = new TicketType(sampleEvent, "General Admission", "Standard entry", new BigDecimal("50.00"), 100, 50, TicketStatus.ACTIVE);
        availableTicket.setId(1L);
        mockTicketTypeDao.save(availableTicket);

        limitedTicket = new TicketType(sampleEvent, "VIP Lounge", "VIP seats", new BigDecimal("150.00"), 10, 2, TicketStatus.ACTIVE);
        limitedTicket.setId(2L);
        mockTicketTypeDao.save(limitedTicket);

        soldOutTicket = new TicketType(sampleEvent, "Early Bird", "Sold out tier", new BigDecimal("30.00"), 50, 0, TicketStatus.ACTIVE);
        soldOutTicket.setId(3L);
        mockTicketTypeDao.save(soldOutTicket);
    }

    @Test
    @DisplayName("Successfully reserves inventory and creates draft booking")
    void testCreateBookingDraftSuccess() {
        CartDto cart = new CartDto();
        CartItemDto item = new CartItemDto(
                availableTicket.getId(), sampleEvent.getId(), sampleEvent.getTitle(),
                availableTicket.getName(), availableTicket.getPrice(), 3, availableTicket.getAvailableQuantity(),
                null, sampleEvent.getEventDate(), sampleEvent.getEventTime(), sampleEvent.getVenue()
        );
        cart.addItem(item);

        Booking booking = bookingService.executeReservationTransaction(null, sampleUser, cart);

        assertNotNull(booking);
        assertTrue(booking.getBookingReference().startsWith("EC-"));
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertEquals(sampleUser.getEmail(), booking.getUser().getEmail());
        assertEquals(new BigDecimal("150.00"), booking.getFinalAmount());
        assertEquals(1, booking.getItems().size());

        // Verify inventory was atomically decremented from 50 to 47
        assertEquals(47, availableTicket.getAvailableQuantity());
    }

    @Test
    @DisplayName("Rejects reservation when requested quantity exceeds available stock (Overselling protection)")
    void testInsufficientStockThrowsInventoryConflict() {
        CartDto cart = new CartDto();
        // limitedTicket has only 2 seats available; customer requests 3
        CartItemDto item = new CartItemDto(
                limitedTicket.getId(), sampleEvent.getId(), sampleEvent.getTitle(),
                limitedTicket.getName(), limitedTicket.getPrice(), 3, limitedTicket.getAvailableQuantity(),
                null, sampleEvent.getEventDate(), sampleEvent.getEventTime(), sampleEvent.getVenue()
        );
        cart.addItem(item);

        InventoryConflictException ex = assertThrows(InventoryConflictException.class, () ->
                bookingService.executeReservationTransaction(null, sampleUser, cart));

        assertTrue(ex.getMessage().contains("Insufficient stock"));
        // Ensure stock was NOT decremented
        assertEquals(2, limitedTicket.getAvailableQuantity());
    }

    @Test
    @DisplayName("Rejects reservation when ticket tier is completely sold out")
    void testSoldOutTicketThrowsInventoryConflict() {
        CartDto cart = new CartDto();
        CartItemDto item = new CartItemDto(
                soldOutTicket.getId(), sampleEvent.getId(), sampleEvent.getTitle(),
                soldOutTicket.getName(), soldOutTicket.getPrice(), 1, 0,
                null, sampleEvent.getEventDate(), sampleEvent.getEventTime(), sampleEvent.getVenue()
        );
        cart.addItem(item);

        assertThrows(InventoryConflictException.class, () ->
                bookingService.executeReservationTransaction(null, sampleUser, cart));
    }

    @Test
    @DisplayName("Rejects reservation if event is unpublished (DRAFT)")
    void testUnpublishedEventThrowsConflict() {
        sampleEvent.setStatus(EventStatus.DRAFT);

        CartDto cart = new CartDto();
        CartItemDto item = new CartItemDto(
                availableTicket.getId(), sampleEvent.getId(), sampleEvent.getTitle(),
                availableTicket.getName(), availableTicket.getPrice(), 1, availableTicket.getAvailableQuantity(),
                null, sampleEvent.getEventDate(), sampleEvent.getEventTime(), sampleEvent.getVenue()
        );
        cart.addItem(item);

        assertThrows(InventoryConflictException.class, () ->
                bookingService.executeReservationTransaction(null, sampleUser, cart));
    }

    @Test
    @DisplayName("Rejects reservation if ticket tier is inactive")
    void testInactiveTicketThrowsConflict() {
        availableTicket.setStatus(TicketStatus.INACTIVE);

        CartDto cart = new CartDto();
        CartItemDto item = new CartItemDto(
                availableTicket.getId(), sampleEvent.getId(), sampleEvent.getTitle(),
                availableTicket.getName(), availableTicket.getPrice(), 1, availableTicket.getAvailableQuantity(),
                null, sampleEvent.getEventDate(), sampleEvent.getEventTime(), sampleEvent.getVenue()
        );
        cart.addItem(item);

        assertThrows(InventoryConflictException.class, () ->
                bookingService.executeReservationTransaction(null, sampleUser, cart));
    }

    @Test
    @DisplayName("Rejects checkout with null user")
    void testNullUserThrowsValidationException() {
        CartDto cart = new CartDto();
        assertThrows(ValidationException.class, () ->
                bookingService.createBookingDraft(null, cart));
    }

    @Test
    @DisplayName("Rejects checkout with empty cart")
    void testEmptyCartThrowsValidationException() {
        CartDto emptyCart = new CartDto();
        assertThrows(ValidationException.class, () ->
                bookingService.createBookingDraft(sampleUser, emptyCart));
    }

    // -------------------------------------------------------------------------
    // Mock DAOs
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
            if (entity.getId() == null) entity.setId((long) (store.size() + 1));
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
            return Collections.emptyList();
        }

        @Override
        public List<TicketType> findActiveByEvent(Long eventId) {
            return Collections.emptyList();
        }

        @Override
        public boolean updateAvailableQuantity(Long ticketTypeId, int deltaQuantity) {
            TicketType t = store.get(ticketTypeId);
            if (t == null) return false;
            t.setAvailableQuantity(t.getAvailableQuantity() + deltaQuantity);
            return true;
        }

        @Override
        public Optional<TicketType> findByIdWithLock(Session session, Long ticketTypeId) {
            return Optional.ofNullable(store.get(ticketTypeId));
        }
    }

    private static class MockBookingDao implements BookingDao {
        private final Map<Long, Booking> store = new HashMap<>();

        @Override
        public Optional<Booking> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Booking> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public List<Booking> findPaginated(int page, int pageSize) {
            return new ArrayList<>(store.values());
        }

        @Override
        public Booking save(Booking entity) {
            if (entity.getId() == null) entity.setId((long) (store.size() + 1));
            store.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Booking update(Booking entity) {
            store.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public void delete(Booking entity) {
            if (entity != null) store.remove(entity.getId());
        }

        @Override
        public void deleteById(Long id) {
            store.remove(id);
        }

        @Override
        public long count() {
            return store.size();
        }

        @Override
        public Optional<Booking> findByReference(String bookingReference) {
            for (Booking b : store.values()) {
                if (Objects.equals(b.getBookingReference(), bookingReference)) {
                    return Optional.of(b);
                }
            }
            return Optional.empty();
        }

        @Override
        public List<Booking> findByUser(Long userId) {
            List<Booking> list = new ArrayList<>();
            for (Booking b : store.values()) {
                if (b.getUser() != null && Objects.equals(b.getUser().getId(), userId)) {
                    list.add(b);
                }
            }
            return list;
        }
    }
}
