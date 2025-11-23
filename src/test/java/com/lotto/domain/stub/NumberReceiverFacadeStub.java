package com.lotto.domain.stub;

import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.numberreceiver.TicketNotFoundException;
import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NumberReceiverFacadeStub implements NumberReceiver {

    private final List<TicketDto> tickets = new ArrayList<>();

    public void addTicket(TicketDto ticket) {
        tickets.add(ticket);
    }

    @Override
    public List<TicketDto> fetchAllTicketDtos(LocalDateTime date) {
        return tickets.stream()
                .filter(t -> t.drawDate().isEqual(date))
                .toList();
    }

    @Override
    public InputNumberResultDto inputNumbers(Set<Integer> numbersFromUser) {
        return null;
    }

    @Override
    public LocalDateTime generateNextDrawDate(LocalDateTime now) {
        return null;
    }

    @Override
    public TicketDto fetchTicketByHash(final String hash) {
        for (TicketDto ticket : tickets) {
            if (ticket.hash().equals(hash)) {
                return ticket;
            }
        }
        throw new TicketNotFoundException("Ticket not found for hash: " + hash);
    }
}

