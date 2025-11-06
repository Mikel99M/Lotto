package com.lotto.domain.numberreceiver;

import java.time.LocalDateTime;
import java.util.Set;

record Ticket(
        String hash,
        LocalDateTime drawDate,
        Set<Integer> numbersFromUser) {
}
