package com.lotto.http.numberGenerator;

import com.lotto.domain.numbergenerator.RandomNumberGenerable;
import com.lotto.infrastructure.numbergenerator.http.RandomGeneratorClientConfig;
import com.lotto.infrastructure.numbergenerator.http.RandomGeneratorClientConfigProperties;
import com.lotto.infrastructure.numbergenerator.http.RandomNumberGeneratorRestTemplate;
import com.lotto.infrastructure.numbergenerator.http.RestTemplateResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class RandomNumberGeneratorRestTemplateIntegrationTestConfig extends RandomGeneratorClientConfig {

    public RandomNumberGenerable remoteNumberGeneratorClient(RandomGeneratorClientConfigProperties properties, RestTemplateResponseErrorHandler errorHandler ) {
        RestTemplate restTemplate = restTemplate(properties, errorHandler);
        return new RandomNumberGeneratorRestTemplate(restTemplate, properties);
    }

}
