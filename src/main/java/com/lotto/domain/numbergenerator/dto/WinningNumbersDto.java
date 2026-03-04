package com.lotto.domain.numbergenerator.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record WinningNumbersDto(
        Set<Integer> winningNumbers,
        Instant date
) {
}
