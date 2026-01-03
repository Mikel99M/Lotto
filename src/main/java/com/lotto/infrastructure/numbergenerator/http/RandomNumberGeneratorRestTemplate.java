package com.lotto.infrastructure.numbergenerator.http;

import com.lotto.domain.numbergenerator.RandomNumberGenerable;
import com.lotto.domain.numbergenerator.SixRandomNumbersDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Log4j2
@Component
@Profile("!local")
public class RandomNumberGeneratorRestTemplate implements RandomNumberGenerable {

    public static final int MAXIMAL_WINNING_NUMBERS = 6;
    public static final String RANDOM_NUMBER_SERVICE_PATH = "/api/v1.0/random";
    private static final int MAX_RETRIES = 10;
    private final RestTemplate restTemplate;

    private final RandomNumberGeneratorProperties properties;

    @Override
    public SixRandomNumbersDto generateSixRandomNumbers(int lowerBand, int upperBand) {
        log.info("Started fetching winning numbers using http client");

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            ResponseEntity<List<Integer>> response = makeGetRequest(lowerBand, upperBand);
            Set<Integer> sixDistinctNumbers = getSixRandomDistinctNumbers(response);
            if (sixDistinctNumbers.size() == MAXIMAL_WINNING_NUMBERS) {
                return new SixRandomNumbersDto(sixDistinctNumbers);
            } else {
                log.warn("Set is less than: {} Have to request one more time", MAXIMAL_WINNING_NUMBERS);
            }
        }

        throw new IllegalStateException("Unable to generate six random numbers. Number of attempts: %d".formatted(MAX_RETRIES));
    }

    private ResponseEntity<List<Integer>> makeGetRequest(int lowerBand, int upperBand) {
        final String url = UriComponentsBuilder.fromHttpUrl(getUrlForService(RANDOM_NUMBER_SERVICE_PATH))
                .queryParam("min", lowerBand)
                .queryParam("max", upperBand)
                .queryParam("count", MAXIMAL_WINNING_NUMBERS)
                .toUriString();
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<>() {
                });
    }

    private Set<Integer> getSixRandomDistinctNumbers(ResponseEntity<List<Integer>> response) {
        List<Integer> numbers = response.getBody();
        if (numbers == null) {
            log.error("Response Body was null returning empty collection");
            return Collections.emptySet();
        }
        log.info("Success Response Body Returned: " + response);
        Set<Integer> distinctNumbers = new HashSet<>(numbers);
        return distinctNumbers.stream()
                .limit(MAXIMAL_WINNING_NUMBERS)
                .collect(Collectors.toSet());
    }

    private String getUrlForService(String service) {
        return properties.getUri() + ":" + properties.getPort() + service;
    }
}
