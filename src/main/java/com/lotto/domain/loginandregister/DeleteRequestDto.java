package com.lotto.domain.loginandregister;

import lombok.Builder;

@Builder
public record DeleteRequestDto(
        String password,
        String email
) {
}
