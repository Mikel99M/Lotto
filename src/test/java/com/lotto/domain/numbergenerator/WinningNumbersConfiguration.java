package com.lotto.domain.numbergenerator;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerator;
import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.general.NumbersGenerable;
import com.lotto.domain.stub.NumberReceiverFacadeStub;

class WinningNumbersConfiguration {

    public WinningNumbersGeneratorFacade createForTest(
            WinningNumbersRepository repository,
            NumbersGenerable numbersGenerator,
            DrawDateGenerator drawDateGenerator) {

        NumberReceiver numberReceiver = new NumberReceiverFacadeStub();

        return new WinningNumbersGeneratorFacade(
//                numberReceiver,
                repository,
                numbersGenerator,
                new WinningNumbersMapper(),
                new HashGenerator(),
                drawDateGenerator
        );
    }
}
