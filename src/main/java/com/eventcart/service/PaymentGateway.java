package com.eventcart.service;

import com.eventcart.dto.PaymentRequest;
import com.eventcart.dto.PaymentResult;

/**
 * Payment gateway abstraction representing integration boundary for Phase 04 payment processing.
 */
public interface PaymentGateway {

    PaymentResult processPayment(PaymentRequest request);
}
