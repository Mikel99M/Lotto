package com.lotto.domain.numberreceiver;

import com.lotto.domain.numberreceiver.dto.TicketDto;

class TicketMapper {

    public static TicketDto mapFromTicket(Ticket ticket) {
        return TicketDto.builder()
                .hash(ticket.hash())
                .drawDate(ticket.drawDate())
                .numbers(ticket.numbersFromUser())
                .build();
    }

}
