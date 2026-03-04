package com.lotto.domain.resultannouncer.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record ResponseDto(
        String hash,
        Set<Integer> numbers,
        Set<Integer> winningNumbers,
        Instant drawDate,
        boolean isWon
) {
}
