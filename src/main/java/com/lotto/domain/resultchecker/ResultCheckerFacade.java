package com.lotto.domain.resultchecker;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.general.WinningNumbersGenerator;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import com.lotto.domain.numberreceiver.NumberReceiverFacade;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultchecker.dto.ResultDto;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class ResultCheckerFacade {

    private final WinningNumbersGenerator winningNumbersGenerator;
    private final NumberReceiverFacade numberReceiver;
    private final DrawDateGenerator drawDateGenerator;

    public ResultDto checkResult(LocalDate drawDate) {

        LocalDateTime exactDrawDate = drawDateGenerator.convertToDateTime(drawDate);

        return ResultDto.builder()
                .drawDate(exactDrawDate)
                .winningTickets(retrieveWinningTickets(drawDate))
                .build();
    }

    public List<TicketDto> retrieveWinningTickets(LocalDate drawDate) {
        Optional<WinningNumbersDto> winningNumbersDto = winningNumbersGenerator
                .retrieveAllWinningNumbersDtos()
                .stream()
                .filter(dto -> dto.date().toLocalDate().isEqual(drawDate))
                .findFirst();

        if (winningNumbersDto.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> winningNumbers = winningNumbersDto.get().winningNumbers();

        LocalDateTime exactDrawDate = drawDateGenerator.convertToDateTime(drawDate);

        return numberReceiver.fetchAllTicketDtos(exactDrawDate)
                .stream()
                .filter(ticket -> ticket.numbers().equals(winningNumbers))
                .toList();
    }

    public boolean thereIsWinningTicket(LocalDate drawDate) {
        return !retrieveWinningTickets(drawDate).isEmpty();
    }

    public Optional<TicketDto> findWinningTicketByHash(String hash) {
        TicketDto ticketDto = numberReceiver.fetchTicketByHash(hash);

        LocalDate drawDate = ticketDto.drawDate().toLocalDate();
        List<TicketDto> winningTickets = retrieveWinningTickets(drawDate);

        return winningTickets.stream()
                .filter(winningTicket -> winningTicket.hash().equals(hash))
                .findFirst();
    }

}
