package com.lotto.domain.resultchecker;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.general.WinningNumbersGenerator;

class ResultCheckerConfiguration {

    public ResultCheckerFacade createForTests(NumberReceiver numberReceiverFacade, WinningNumbersGenerator winningNumbersGenerator) {

//        NumberReceiverFacade numberReceiverFacade = new NumberReceiverFacadeStub();

        return new ResultCheckerFacade(
                winningNumbersGenerator,
                numberReceiverFacade,
                new DrawDateGenerator()
        );
    }
}
