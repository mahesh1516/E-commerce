package com.nexora.ecommerce.util;

import com.nexora.ecommerce.dto.PriceBreakdown;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PriceCalculatorTest {

    private final PriceCalculator calculator =
            new PriceCalculator(new BigDecimal("0.10"), new BigDecimal("50"), new BigDecimal("5000"));

    @Test
    void calculatesSpecExample_2000_minus200_plusTax180_plusShipping50_equals2030() {
        // 2 units, list price 1000, sale price 900
        PriceBreakdown result = calculator.calculate(List.of(
                new PriceCalculator.Line(new BigDecimal("1000"), new BigDecimal("900"), 2)));

        assertThat(result.subtotal()).isEqualByComparingTo("2000");
        assertThat(result.discount()).isEqualByComparingTo("200");
        assertThat(result.tax()).isEqualByComparingTo("180");
        assertThat(result.shipping()).isEqualByComparingTo("50");
        assertThat(result.grandTotal()).isEqualByComparingTo("2030");
    }

    @Test
    void freeShippingAboveThreshold() {
        PriceBreakdown result = calculator.calculate(List.of(
                new PriceCalculator.Line(new BigDecimal("6000"), new BigDecimal("6000"), 1)));

        assertThat(result.shipping()).isEqualByComparingTo("0");
        assertThat(result.grandTotal()).isEqualByComparingTo("6600");
    }

    @Test
    void emptyCartCostsNothing() {
        PriceBreakdown result = calculator.calculate(List.of());

        assertThat(result.grandTotal()).isEqualByComparingTo("0");
        assertThat(result.shipping()).isEqualByComparingTo("0");
    }
}
