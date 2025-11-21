package com.lotto.domain.stub;

import com.lotto.domain.general.NumbersGenerable;

import java.util.Set;

public class NumbersGeneratorStub implements NumbersGenerable {

    public Set<Integer> generateSixNumbers() {
        return Set.of(1, 2, 3, 4, 5, 6);
    }
}
