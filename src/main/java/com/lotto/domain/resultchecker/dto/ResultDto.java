package com.lotto.domain.resultchecker.dto;

import com.lotto.domain.numberreceiver.dto.TicketDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ResultDto(
        LocalDateTime drawDate,
        List<TicketDto> winningTickets
) {
}
