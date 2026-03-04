package com.lotto.domain.numberreceiver.dto;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Set;

@Builder
public record InputNumberResultDto(
        String message,
        ZonedDateTime operationDate,
        ZonedDateTime drawDate,
        String hash,
        Set<Integer> numbersFromUser
) {
}
