package com.lotto.infrastructure.resultchecker.scheduler;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.resultchecker.ResultCheckerFacade;
import com.lotto.domain.resultchecker.dto.ResultDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
@AllArgsConstructor
@Log4j2
public class ResultCheckerScheduler {

    private final ResultCheckerFacade facade;
    private final DrawDateGenerator drawDateGenerator;
    private final Clock clock;

    @Scheduled(cron = "${lotto.number-generator.checkingLotterryResultsOccurrence}")
    public ResultDto run() {
        log.info("ResultCheckerScheduler started");
        Instant now = Instant.now(clock);
        Instant drawDate = drawDateGenerator.generatePreviousDrawDate(now);
        ResultDto resultDto = facade.checkResult(drawDate);
        log.info("Result generated for draw date: " + drawDate);
        log.info("Result contains tickets: " + resultDto.winningTickets());
        return resultDto;
    }

}
