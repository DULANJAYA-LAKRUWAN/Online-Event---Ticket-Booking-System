package com.eventcart.service.impl;

import com.eventcart.dto.PaymentRequest;
import com.eventcart.dto.PaymentResult;
import com.eventcart.service.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Sandbox Mock implementation of PaymentGateway for testing and preparation ahead of Phase 04.
 */
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request == null || request.getAmount() == null || request.getBookingReference() == null) {
            return PaymentResult.failure(null, "Invalid payment request parameters.");
        }

        String paymentRef = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String txnId = "TXN-" + System.currentTimeMillis();

        logger.info("Mock Payment initiated for booking '{}', amount: LKR {}, ref: '{}'",
                request.getBookingReference(), request.getAmount(), paymentRef);

        return PaymentResult.success(paymentRef, txnId, "Payment authorized successfully (Sandbox Mock).");
    }
}
