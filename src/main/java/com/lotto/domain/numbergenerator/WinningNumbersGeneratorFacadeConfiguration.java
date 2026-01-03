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
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade(
            WinningNumbersRepository repository,
            RandomNumberGenerable numbersGenerator,
            HashGenerable hashGenerator,
            Clock clock,
            WinningNumbersGeneratorFacadeConfigurationProperties properties) {

        WinningNumbersMapper mapper = new WinningNumbersMapper();
        DrawDateGenerator drawDateGenerator = new DrawDateGenerator();

        return new WinningNumbersGeneratorFacade(
                repository,
                numbersGenerator,
                mapper,
                hashGenerator,
                drawDateGenerator,
                properties
        );
    }

    public WinningNumbersGeneratorFacade createForTest(
            WinningNumbersRepository repository,
            RandomNumberGenerable numbersGenerator,
            DrawDateGenerator drawDateGenerator) {


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
                properties
        );
    }
}
