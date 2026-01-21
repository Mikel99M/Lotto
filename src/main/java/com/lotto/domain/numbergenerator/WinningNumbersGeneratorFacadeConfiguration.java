package com.lotto.domain.numbergenerator;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerable;
import com.lotto.domain.general.HashGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class WinningNumbersGeneratorFacadeConfiguration {

    @Bean
    HashGenerable hashGenerator() {
        return new HashGenerator();
    }

    @Bean
    DrawDateGenerator drawDateGenerator() {
        return new DrawDateGenerator();
    }

    @Bean
    WinningNumbersMapper winningNumbersMapper() {
        return new WinningNumbersMapper();
    }

    @Bean
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade(
            WinningNumbersRepository repository,
            RandomNumberGenerable numbersGenerator,
            HashGenerable hashGenerator,
            DrawDateGenerator drawDateGenerator,
            WinningNumbersMapper mapper,
            WinningNumbersGeneratorFacadeConfigurationProperties properties,
            Clock clock) {

        return new WinningNumbersGeneratorFacade(
                repository,
                numbersGenerator,
                mapper,
                hashGenerator,
                drawDateGenerator,
                properties,
                clock
        );
    }

    public WinningNumbersGeneratorFacade createForTest(
            WinningNumbersRepository repository,
            RandomNumberGenerable numbersGenerator,
            DrawDateGenerator drawDateGenerator,
            Clock clock) {


        WinningNumbersGeneratorFacadeConfigurationProperties properties = WinningNumbersGeneratorFacadeConfigurationProperties.builder()
                .lowerBand(1)
                .upperBand(99)
                .build();

        return new WinningNumbersGeneratorFacade(
                repository,
                numbersGenerator,
                new WinningNumbersMapper(),
                new HashGenerator(),
                drawDateGenerator,
                properties,
                clock
        );
    }
}
