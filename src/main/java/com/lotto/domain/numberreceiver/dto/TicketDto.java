package com.lotto.domain.numberreceiver.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record TicketDto(
        String hash,
        Instant purchaseDate,
        Instant drawDate,
        Set<Integer> numbers
) {
}
