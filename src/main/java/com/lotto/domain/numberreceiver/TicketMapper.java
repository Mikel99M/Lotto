package com.lotto.domain.numberreceiver;

import com.lotto.domain.numberreceiver.dto.TicketDto;

class TicketMapper {

    public static TicketDto mapFromTicketToTicketDto(Ticket ticket) {
        return TicketDto.builder()
                .hash(ticket.hash())
                .purchaseDate(ticket.purchaseDate())
                .drawDate(ticket.drawDate())
                .numbers(ticket.numbersFromUser())
                .build();
    }

}
