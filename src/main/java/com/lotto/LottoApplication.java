package com.lotto;

import com.lotto.domain.general.TimeProperties;
import com.lotto.domain.numbergenerator.WinningNumbersGeneratorFacadeConfigurationProperties;
import com.lotto.infrastructure.numbergenerator.http.RandomGeneratorClientConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableConfigurationProperties({WinningNumbersGeneratorFacadeConfigurationProperties.class, RandomGeneratorClientConfigProperties.class, TimeProperties.class})
@EnableMongoRepositories
public class LottoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LottoApplication.class, args);

    }
}
