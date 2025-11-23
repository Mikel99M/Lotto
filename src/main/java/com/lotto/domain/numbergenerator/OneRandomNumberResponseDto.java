package com.lotto.domain.numbergenerator;

import lombok.Builder;

@Builder
public record OneRandomNumberResponseDto(
        int number
) {
}
