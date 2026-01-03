package com.lotto.domain.numbergenerator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Component("secureRandomNumberGenerator")
//@Profile("local")
public class SecureRandomNumberGenerator implements RandomNumberGenerable {

    private static final int REQUIRED_NUMBERS = 6;
    private final SecureRandom random = new SecureRandom();

    @Override
    public SixRandomNumbersDto generateSixRandomNumbers(int lowerBand, int upperBand) {
        Set<Integer> numbers = new HashSet<>();

        while (numbers.size() < REQUIRED_NUMBERS) {
            int value = random.nextInt(upperBand - lowerBand + 1) + lowerBand;
            numbers.add(value);
        }

        return SixRandomNumbersDto.builder()
                .numbers(numbers)
                .build();
    }
}

