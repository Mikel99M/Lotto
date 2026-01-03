package com.lotto.infrastructure.numbergenerator.http;

import com.lotto.domain.numbergenerator.RandomNumberGenerable;
import com.lotto.domain.numbergenerator.SixRandomNumbersDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class FallbackRandomNumberGenerator implements RandomNumberGenerable {

    private final RandomNumberGenerable httpGenerator;
    private final RandomNumberGenerable secureGenerator;

    public FallbackRandomNumberGenerator(
            @Qualifier("randomNumberGeneratorRestTemplate") RandomNumberGenerable httpGenerator,
            @Qualifier("secureRandomNumberGenerator") RandomNumberGenerable secureGenerator
    ) {
        this.httpGenerator = httpGenerator;
        this.secureGenerator = secureGenerator;
    }

    @Override
    public SixRandomNumbersDto generateSixRandomNumbers(final int lowerBand, final int upperBand) {
        try {
            return httpGenerator.generateSixRandomNumbers(lowerBand, upperBand);
        } catch (Exception e) {
            return secureGenerator.generateSixRandomNumbers(lowerBand, upperBand);
        }
    }
}
