package com.lotto.domain.stub;

import com.lotto.domain.numbergenerator.RandomNumberGenerable;
import com.lotto.domain.numbergenerator.SixRandomNumbersDto;

import java.util.Set;

public class NumbersGeneratorStub implements RandomNumberGenerable {

    public SixRandomNumbersDto generateSixRandomNumbers(final int lowerBand, final int upperBand) {
        return new SixRandomNumbersDto(Set.of(1, 2, 3, 4, 5, 6));
    }

}
