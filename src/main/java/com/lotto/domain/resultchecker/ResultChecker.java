package com.lotto.domain.resultchecker;

import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultchecker.dto.ResultDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ResultChecker {

    ResultDto checkResult(LocalDate drawDate);

    List<TicketDto> retrieveWinningTickets(LocalDate drawDate);

    Optional<TicketDto> findWinningTicketByHash(String hash);

    boolean thereIsWinningTicket(LocalDate drawDate);

}
