package com.lotto.domain.numbergenerator;

import com.lotto.domain.general.NumbersGenerable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@AllArgsConstructor
public class NumbersGenerator implements NumbersGenerable {

    private final Random random;

    public Set<Integer> generateSixNumbers() {
        int MIN = 1, MAX = 99;
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < 6) {
            numbers.add(random.nextInt(MAX) + MIN);
        }
        return numbers;
    }

}

