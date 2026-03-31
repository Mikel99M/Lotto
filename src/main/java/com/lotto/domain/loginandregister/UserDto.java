package com.lotto.domain.loginandregister;

import lombok.Builder;

@Builder
public record UserDto(
        String name,
        String email,
        String password

) {
}
