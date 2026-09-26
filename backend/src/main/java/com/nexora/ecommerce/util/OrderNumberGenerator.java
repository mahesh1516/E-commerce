package com.nexora.ecommerce.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class OrderNumberGenerator {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private OrderNumberGenerator() {
    }

    /** e.g. NX20260926-7KQ4PZ */
    public static String next() {
        StringBuilder sb = new StringBuilder("NX")
                .append(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                .append('-');
        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(ThreadLocalRandom.current().nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
