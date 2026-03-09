package com.lotto.domain.resultchecker;

import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.numbergenerator.WinningNumbers;
import com.lotto.domain.numbergenerator.WinningNumbersRepository;
import com.lotto.domain.numberreceiver.TicketNotFoundException;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultchecker.dto.ResultDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class ResultCheckerFacade implements ResultChecker {

    private final NumberReceiver numberReceiver;
    private final WinningNumbersRepository numbersRepository;

    public ResultDto checkResult(Instant drawDate) {

        return ResultDto.builder()
                .drawDate(drawDate)
                .winningTickets(retrieveWinningTickets(drawDate))
                .build();
    }

    public List<TicketDto> retrieveWinningTickets(Instant drawDate) {
        Optional<WinningNumbers> winningNumbers = numbersRepository.findWinningNumbersByDate(drawDate);

        if (winningNumbers.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> sixWinningNumbers = winningNumbers.get().numbers();

        return numberReceiver.fetchAllTicketDtos(drawDate)
                .stream()
                .filter(ticket -> ticket.numbers().equals(sixWinningNumbers))
                .toList();
    }

    public Optional<TicketDto> findWinningTicketByHash(String hash) {
        try {
        TicketDto ticketDto = numberReceiver.fetchTicketByHash(hash);
        Instant drawDateTime = ticketDto.drawDate();
        List<TicketDto> winningTickets = retrieveWinningTickets(drawDateTime);

        return winningTickets.stream()
                .filter(winningTicket -> winningTicket.hash().equals(hash))
                .findFirst();
        } catch (TicketNotFoundException e) {
            return Optional.empty();
        }
    }

    public boolean thereIsWinningTicket(LocalDate drawDate) {
        Instant drawDateInstant = drawDate.atStartOfDay().atZone(ZoneId.of("Europe/Warsaw")).toInstant();
        return !retrieveWinningTickets(drawDateInstant).isEmpty();
    }

}
