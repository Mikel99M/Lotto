package com.lotto.domain.numberreceiver.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record InputNumberResultDto(
        String message,
        LocalDateTime drawDate,
        String hash,
        Set<Integer> numbersFromUser
) {
}
