package com.eventcart.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Encapsulates payment initiation request details for the PaymentGateway.
 */
public class PaymentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bookingReference;
    private Long userId;
    private String customerEmail;
    private BigDecimal amount;
    private String paymentMethod;

    public PaymentRequest() {
    }

    public PaymentRequest(String bookingReference, Long userId, String customerEmail, BigDecimal amount, String paymentMethod) {
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.paymentMethod = paymentMethod != null ? paymentMethod : "MOCK_SANDBOX";
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
