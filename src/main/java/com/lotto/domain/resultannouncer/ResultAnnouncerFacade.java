package com.lotto.domain.resultannouncer;

import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.general.WinningNumbersGenerator;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import com.lotto.domain.numberreceiver.TicketNotFoundException;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultannouncer.dto.ResponseDto;
import com.lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;
import com.lotto.domain.resultchecker.ResultChecker;
import lombok.AllArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class ResultAnnouncerFacade {

    private final ResultChecker resultCheckerFacade;
    private final NumberReceiver numberReceiverFacade;
    private final WinningNumbersGenerator WinningNumbersGeneratorFacade;
    private final Clock clock;

    public ResultAnnouncerResponseDto checkResult(String ticketHash) {

        TicketDto ticketDto;

        try {
            ticketDto = numberReceiverFacade.fetchTicketByHash(ticketHash);
        } catch (TicketNotFoundException e) {
            return response(null, false, ResponseMessages.NO_TICKET_WITH_THIS_HASH_FOUND.info);
        }

        if (isBeforeDrawDate(ticketDto.drawDate())) {
            return response(ticketDto, false, ResponseMessages.IT_IS_BEFORE_DRAW_DATE.info);
        }

        Optional<TicketDto> winningTicket = resultCheckerFacade.findWinningTicketByHash(ticketHash);

        if (winningTicket.isPresent()) {
            return response(winningTicket.get(), true, ResponseMessages.TICKET_HAS_WON.info);
        }

        return response(ticketDto, false, ResponseMessages.TICKET_HAS_LOST.info);
    }

    private boolean isBeforeDrawDate(LocalDateTime drawDate) {
        return LocalDateTime.now(clock).isBefore(drawDate);
    }

    private ResultAnnouncerResponseDto response(TicketDto ticket, boolean isWinner, String message) {

        String hash = null;
        Set<Integer> numbers = null;
        Set<Integer> winningNumbers = Set.of();
        LocalDateTime drawDate = null;

        if (ticket != null) {
            hash = ticket.hash();
            numbers = ticket.numbers();
            drawDate = ticket.drawDate();
            winningNumbers = getWinningNumbers(ticket);

        }

        ResponseDto dto = new ResponseDto(
                hash,
                numbers,
                winningNumbers,
                drawDate,
                isWinner
        );

        return new ResultAnnouncerResponseDto(dto, message);
    }

    private Set<Integer> getWinningNumbers(TicketDto ticket) {
        WinningNumbersDto result = WinningNumbersGeneratorFacade.retrieveWinningNumbersDtoByDraw(ticket.drawDate());
        return result.winningNumbers();
    }

}