package com.lotto.domain.general.error;

import org.springframework.http.HttpStatus;

record ErrorResponseMessage(
        String response,
        HttpStatus status
) {
}
