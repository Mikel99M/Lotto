package com.lotto.infrastructure.numbergenerator.scheduler;

import com.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class WinningNumbersScheduler {

    private final WinningNumbersGeneratorFacade facade;

    @Scheduled(cron = "${lotto.number-generator.lotteryRunOccurrence}")
    public WinningNumbersDto run() {
        log.info("WinningNumbersScheduler scheduler started");
        WinningNumbersDto winningNumbersDto = facade.generate();
        log.info("Winning numbers: " + winningNumbersDto.winningNumbers());
        log.info("Date: " + winningNumbersDto.date());
        return winningNumbersDto;
    }

}
