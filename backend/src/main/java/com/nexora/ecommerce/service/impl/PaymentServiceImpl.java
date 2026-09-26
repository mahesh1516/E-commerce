package com.nexora.ecommerce.service.impl;

import com.nexora.ecommerce.entity.Order;
import com.nexora.ecommerce.entity.Payment;
import com.nexora.ecommerce.entity.PaymentMethod;
import com.nexora.ecommerce.entity.PaymentStatus;
import com.nexora.ecommerce.exception.InvalidRequestException;
import com.nexora.ecommerce.exception.PaymentFailedException;
import com.nexora.ecommerce.service.PaymentService;
import com.nexora.ecommerce.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * FAKE payment gateway for learning.
 * CARD: number ending in 0000 is declined, anything else succeeds.
 * COD : payment stays PENDING until the order is delivered.
 * Only the last 4 card digits are stored; nothing is logged.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public Payment processPayment(Order order, PaymentMethod method, String cardNumber) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getGrandTotal());
        payment.setMethod(method);

        if (method == PaymentMethod.CARD) {
            if (cardNumber == null || cardNumber.isBlank()) {
                throw new InvalidRequestException("Card number is required for card payments");
            }
            if (cardNumber.endsWith(AppConstants.DECLINED_CARD_SUFFIX)) {
                log.warn("Simulated card payment declined for order {}", order.getOrderNumber());
                throw new PaymentFailedException(
                        "Payment declined (simulation). You were not charged - please try another card.");
            }
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCardLast4(cardNumber.substring(cardNumber.length() - 4));
            payment.setTransactionId(newTransactionId());
        } else {
            payment.setStatus(PaymentStatus.PENDING);
        }

        order.setPayment(payment);
        log.info("Payment {} for order {} via {}", payment.getStatus(), order.getOrderNumber(), method);
        return payment;
    }

    static String newTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
