package com.eventcart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment transaction record associated with a booking.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_booking", columnList = "booking_id")
})
public class Payment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "payment_reference", nullable = false, unique = true, length = 50)
    private String paymentReference;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod = "MOCK_SANDBOX";

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    public Payment() {
    }

    public Payment(Booking booking, String paymentReference, String paymentMethod, BigDecimal amount, PaymentStatus status) {
        this.booking = booking;
        this.paymentReference = paymentReference;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "MOCK_SANDBOX";
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.status = status != null ? status : PaymentStatus.INITIATED;
        this.paymentDate = LocalDateTime.now();
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }
}
