package com.lotto.domain.numberreceiver;

import java.time.Instant;
import java.util.Set;

record Ticket(
        String hash,
        Instant purchaseDate,
        Instant drawDate,
        Set<Integer> numbersFromUser) {
}
