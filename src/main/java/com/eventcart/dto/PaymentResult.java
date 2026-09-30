package com.eventcart.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Encapsulates the response from the PaymentGateway processing attempt.
 */
public class PaymentResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean successful;
    private String paymentReference;
    private String transactionId;
    private String message;
    private LocalDateTime timestamp;

    public PaymentResult() {
        this.timestamp = LocalDateTime.now();
    }

    public PaymentResult(boolean successful, String paymentReference, String transactionId, String message) {
        this.successful = successful;
        this.paymentReference = paymentReference;
        this.transactionId = transactionId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static PaymentResult success(String paymentReference, String transactionId, String message) {
        return new PaymentResult(true, paymentReference, transactionId, message);
    }

    public static PaymentResult failure(String paymentReference, String message) {
        return new PaymentResult(false, paymentReference, null, message);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
