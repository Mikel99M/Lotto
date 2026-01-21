package com.lotto.domain.numberreceiver;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerable;
import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import lombok.AllArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class NumberReceiverFacade implements NumberReceiver {

    private final NumberValidator numberValidator;
    private final TicketRepository repository;
    private final Clock clock;
    private final HashGenerable hashGenerator;
    private final DrawDateGenerator drawDateGenerator;

    public InputNumberResultDto inputNumbers(Set<Integer> numbersFromUser) {
        boolean areAllNumbersInRange = areNumbersCorrectAndInRange(numbersFromUser);
        LocalDateTime now = LocalDateTime.now(clock);
        if (areAllNumbersInRange) {
            String hash = generateHash();
            LocalDateTime drawDate = generateNextDrawDate(now);
            Ticket save = repository.save(new Ticket(hash, now, drawDate, numbersFromUser));

            return InputNumberResultDto.builder()
                    .message("success")
                    .operationDate(now)
                    .drawDate(save.drawDate())
                    .hash(save.hash())
                    .numbersFromUser(save.numbersFromUser())
                    .build();
        }
        return InputNumberResultDto.builder()
                .message("failed")
                .operationDate(now)
                .build();
    }

    private boolean areNumbersCorrectAndInRange(final Set<Integer> numbersFromUser) {
        return numberValidator.areNumbersCorrectAndInRange(numbersFromUser);
    }

    public List<TicketDto> fetchAllTicketDtos(LocalDateTime date) {
        List<Ticket> allTicketsByDrawDate = repository.findAllTicketsByDrawDate(date);
        return allTicketsByDrawDate
                .stream()
                .map(TicketMapper::mapFromTicket)
                .toList();
    }

    @Override
    public TicketDto fetchTicketByHash(final String hash) {
        Ticket ticket = repository.findByHash(hash)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found for hash: " + hash));

        return TicketDto.builder()
                .numbers(ticket.numbersFromUser())
                .purchaseDate(ticket.purchaseDate())
                .drawDate(ticket.drawDate())
                .hash(ticket.hash())
                .build();
    }

    public LocalDateTime generateNextDrawDate(LocalDateTime NOW) {
        return drawDateGenerator.generateNextDrawDate(NOW);
    }

    public String generateHash() {
        return hashGenerator.generateHash();
    }
}
