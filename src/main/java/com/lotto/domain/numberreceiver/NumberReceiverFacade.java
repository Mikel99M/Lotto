package com.lotto.domain.numberreceiver;

import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import lombok.AllArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.lotto.domain.numberreceiver.DrawDateGenerator.generateNextDrawDate;

/// klient podate 6 liczb
/// liczby musza byc zakrsie 1-99
/// liczby nie maja sie powtarzac
/// klient dostaje informacje o dacie losowania
/// klient dostaje informajce o swoim indywidualnym identyfikatorze losowania

//@Service
@AllArgsConstructor
public class NumberReceiverFacade {

    private final NumberValidator numberValidator;
    private final TicketRepository repository;
    private final Clock clock ;
    private final HashGenerable hashGenerator;

    public InputNumberResultDto inputNumbers(Set<Integer> numbersFromUser) {
        boolean areAllNumbersInRange = numberValidator.areNumbersCorrectAndInRange(numbersFromUser);
        LocalDateTime now = LocalDateTime.now(clock);
        if (areAllNumbersInRange) {
            String hash = hashGenerator.generateHash();
            LocalDateTime drawDate = generateNextDrawDate(now);
            Ticket save = repository.save(new Ticket(hash, drawDate, numbersFromUser));

            System.out.println(clock.instant().toString());

            return InputNumberResultDto.builder()
                    .message("success")
                    .drawDate(save.drawDate())
                    .hash(save.hash())
                    .numbersFromUser(save.numbersFromUser())
                    .build();
        }
        return InputNumberResultDto.builder()
                .message("failed")
                .build();
    }

    public List<TicketDto> fetchAllTicketDtos(LocalDateTime date) {
        List<Ticket> allTicketsByDrawDate = repository.findAllTicketsByDrawDate(date);
        return allTicketsByDrawDate
                .stream()
                .map(TicketMapper::mapFromTicket)
                .toList();
    }
}
