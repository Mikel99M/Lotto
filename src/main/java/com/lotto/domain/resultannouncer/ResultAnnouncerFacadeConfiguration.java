package com.lotto.domain.resultannouncer;

import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.resultchecker.ResultChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class ResultAnnouncerFacadeConfiguration {

    @Bean
    public ResultAnnouncerFacade resultAnnouncerFacade(
            ResultChecker resultChecker,
            NumberReceiver numberReceiver,
            Clock clock) {

        return new ResultAnnouncerFacade(resultChecker, numberReceiver, clock);
    }

}
