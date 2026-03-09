package com.lotto.domain.numberreceiver;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerable;
import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class NumberReceiverFacade implements NumberReceiver {

    private final NumberValidator numberValidator;
    private final TicketRepository repository;
    private final HashGenerable hashGenerator;
    private final DrawDateGenerator drawDateGenerator;
    private final Clock clock;
    private final ZoneId businessZone;

    public InputNumberResultDto inputNumbers(Set<Integer> numbersFromUser) {
        boolean areAllNumbersInRange = areNumbersCorrectAndInRange(numbersFromUser);
        Instant now = Instant.now(clock);
        if (areAllNumbersInRange) {
            String hash = generateHash();
            Instant drawDate = generateNextDrawDate(now);
            Ticket save = repository.save(Ticket.builder()
                    .hash(hash)
                    .numbersFromUser(numbersFromUser)
                    .drawDate(drawDate)
                    .purchaseDate(now)
                    .build());

            return InputNumberResultDto.builder()
                    .message("success")
                    .operationDate(now.atZone(businessZone))
                    .drawDate(save.drawDate().atZone(businessZone))
                    .hash(save.hash())
                    .numbersFromUser(save.numbersFromUser())
                    .build();
        }
        return InputNumberResultDto.builder()
                .message("failed")
                .operationDate(now.atZone(businessZone))
                .build();
    }

    private boolean areNumbersCorrectAndInRange(final Set<Integer> numbersFromUser) {
        return numberValidator.areNumbersCorrectAndInRange(numbersFromUser);
    }

    public List<TicketDto> fetchAllTicketDtos(Instant date) {
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

    public Instant generateNextDrawDate(Instant NOW) {
        return drawDateGenerator.generateNextDrawDate(NOW);
    }

    public String generateHash() {
        return hashGenerator.generateHash();
    }
}
