package com.nexora.ecommerce.service;

import com.nexora.ecommerce.entity.Order;
import com.nexora.ecommerce.entity.Payment;
import com.nexora.ecommerce.entity.PaymentMethod;

public interface PaymentService {

    /**
     * Simulates a payment and attaches it to the order.
     * Throws PaymentFailedException when the (simulated) card is declined.
     */
    Payment processPayment(Order order, PaymentMethod method, String cardNumber);
}
