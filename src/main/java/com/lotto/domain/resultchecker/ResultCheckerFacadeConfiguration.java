package com.lotto.domain.resultchecker;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.general.WinningNumbersGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ResultCheckerFacadeConfiguration {

    @Bean
    ResultCheckerFacade resultCheckerFacade(
            WinningNumbersGenerator winningNumbersGenerator,
            NumberReceiver numberReceiver
    ) {
        DrawDateGenerator drawDateGeneratorInstance = new DrawDateGenerator();

        return new ResultCheckerFacade(
                winningNumbersGenerator,
                numberReceiver,
                drawDateGeneratorInstance
        );
    }

    public ResultCheckerFacade createForTests(NumberReceiver numberReceiverFacade, WinningNumbersGenerator winningNumbersGenerator) {

        return new ResultCheckerFacade(
                winningNumbersGenerator,
                numberReceiverFacade,
                new DrawDateGenerator()
        );
    }
}
