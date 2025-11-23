package com.lotto.domain.stub;

import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultchecker.ResultChecker;
import com.lotto.domain.resultchecker.dto.ResultDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultCheckerFacadeStub implements ResultChecker {

    List<TicketDto> ticketsThatWon = new ArrayList<>();

    public void addTicketsThatWon(TicketDto ticketDto) {
        ticketsThatWon.add(ticketDto);
    }

    @Override
    public ResultDto checkResult(final LocalDate drawDate) {
        return null;
    }

    @Override
    public List<TicketDto> retrieveWinningTickets(final LocalDate drawDate) {
        return List.of();
    }

    @Override
    public Optional<TicketDto> findWinningTicketByHash(final String hash) {
        for (TicketDto ticketDto : ticketsThatWon) {
            if (ticketDto.hash().equals(hash)) {
                return Optional.of(ticketDto);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean thereIsWinningTicket(final LocalDate drawDate) {
        return false;
    }
}
