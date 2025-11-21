package com.lotto.domain.numbergenerator;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerator;
import com.lotto.domain.general.NumbersGenerable;

class WinningNumbersConfiguration {

    public WinningNumbersGeneratorFacade createForTest(
            WinningNumbersRepository repository,
            NumbersGenerable numbersGenerator,
            DrawDateGenerator drawDateGenerator) {

        return new WinningNumbersGeneratorFacade(
                repository,
                numbersGenerator,
                new WinningNumbersMapper(),
                new HashGenerator(),
                drawDateGenerator
        );
    }
}
