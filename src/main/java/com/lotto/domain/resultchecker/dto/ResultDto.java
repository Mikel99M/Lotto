package com.lotto.domain.resultchecker.dto;

import com.lotto.domain.numberreceiver.dto.TicketDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ResultDto(
        Instant drawDate,
        List<TicketDto> winningTickets
) {
}
