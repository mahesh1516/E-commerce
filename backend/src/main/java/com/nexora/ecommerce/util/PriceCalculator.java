package com.nexora.ecommerce.util;

import com.nexora.ecommerce.dto.PriceBreakdown;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Single place for money math, used by both Cart and Checkout,
 * so the cart page and the final order always agree.
 */
@Component
public class PriceCalculator {

    /** One cart/order line. price = list price, unitPrice = price paid. */
    public record Line(BigDecimal price, BigDecimal unitPrice, int quantity) {
    }

    private final BigDecimal taxRate;
    private final BigDecimal shippingFee;
    private final BigDecimal freeShippingThreshold;

    public PriceCalculator(@Value("${app.pricing.tax-rate}") BigDecimal taxRate,
                           @Value("${app.pricing.shipping-fee}") BigDecimal shippingFee,
                           @Value("${app.pricing.free-shipping-threshold}") BigDecimal freeShippingThreshold) {
        this.taxRate = taxRate;
        this.shippingFee = shippingFee;
        this.freeShippingThreshold = freeShippingThreshold;
    }

    public PriceBreakdown calculate(List<Line> lines) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;

        for (Line line : lines) {
            BigDecimal qty = BigDecimal.valueOf(line.quantity());
            subtotal = subtotal.add(line.price().multiply(qty));
            discount = discount.add(line.price().subtract(line.unitPrice()).multiply(qty));
        }

        BigDecimal taxable = subtotal.subtract(discount);
        BigDecimal tax = taxable.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal shipping;
        if (taxable.signum() == 0 || taxable.compareTo(freeShippingThreshold) > 0) {
            shipping = BigDecimal.ZERO;
        } else {
            shipping = shippingFee;
        }

        BigDecimal grandTotal = taxable.add(tax).add(shipping);

        return new PriceBreakdown(
                money(subtotal), money(discount), money(tax), money(shipping), money(grandTotal));
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
