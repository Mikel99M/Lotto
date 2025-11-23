package com.lotto.domain.resultannouncer;

import com.lotto.domain.numberreceiver.NumberReceiverFacade;
import com.lotto.domain.numberreceiver.TicketNotFoundException;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultannouncer.dto.ResponseDto;
import com.lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;
import com.lotto.domain.resultchecker.ResultCheckerFacade;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class ResultAnnouncerFacade {

    private final ResultCheckerFacade resultCheckerFacade;
    private final NumberReceiverFacade numberReceiverFacade;

    public ResultAnnouncerResponseDto checkResult(String ticketHash) {

        TicketDto ticketDto;

        try {
            ticketDto = numberReceiverFacade.fetchTicketByHash(ticketHash);
        } catch (TicketNotFoundException e) {
            return response(null, false, ResponseMesseges.NO_TICKET_WITH_THIS_HASH_FOUND.info);
        }

        if (isBeforeDrawDate(ticketDto.drawDate())) {
            return response(ticketDto, false, ResponseMesseges.IT_IS_BEFORE_DRAW_DATE.info);
        }

        Optional<TicketDto> winningTicket = resultCheckerFacade.findWinningTicketByHash(ticketHash);

        if (winningTicket.isPresent()) {
            return response(winningTicket.get(), true, ResponseMesseges.TICKET_HAS_WON.info);
        }

        return response(ticketDto, false, ResponseMesseges.TICKET_HAS_LOST.info);
    }

    private boolean isBeforeDrawDate(LocalDateTime drawDate) {
        return LocalDateTime.now().isBefore(drawDate);
    }

    private ResultAnnouncerResponseDto response(TicketDto ticket, boolean isWinner, String message) {

        String hash = null;
        Set<Integer> numbers = null;
        LocalDateTime drawDate = null;

        if (ticket != null) {
            hash = ticket.hash();
            numbers = ticket.numbers();
            drawDate = ticket.drawDate();
        }

        ResponseDto dto = new ResponseDto(
                hash,
                numbers,
                drawDate,
                isWinner
        );

        return new ResultAnnouncerResponseDto(dto, message);
    }

}