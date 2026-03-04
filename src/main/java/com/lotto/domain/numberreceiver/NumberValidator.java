package com.lotto.domain.numberreceiver;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
class NumberValidator {

    private static final int MINIMAL_NUMBERS_FROM_USER = 1;
    private static final int MAXIMAL_NUMBER_FROM_USER = 99;
    private static final int MAX_NUMBERS_FROM_USER = 6;

    Boolean areNumbersCorrectAndInRange(final Set<Integer> numbersFromUser) {
        return numbersFromUser.stream()
                .filter(number -> number >= MINIMAL_NUMBERS_FROM_USER)
                .filter(number -> number <= MAXIMAL_NUMBER_FROM_USER)
                .count() == MAX_NUMBERS_FROM_USER;
    }
}
