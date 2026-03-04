package com.lotto.domain.resultchecker;

import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultchecker.dto.ResultDto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ResultChecker {

    ResultDto checkResult(Instant drawDate);

    List<TicketDto> retrieveWinningTickets(Instant drawDate);

    Optional<TicketDto> findWinningTicketByHash(String hash);

    boolean thereIsWinningTicket(LocalDate drawDate);

}
