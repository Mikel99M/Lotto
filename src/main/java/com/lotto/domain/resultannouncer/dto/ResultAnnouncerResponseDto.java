package com.lotto.domain.resultannouncer.dto;

import lombok.Builder;

@Builder
public record ResultAnnouncerResponseDto(
        ResponseDto response,
        String message
) {
}
