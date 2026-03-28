package com.lotto.http.numberGenerator;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.lotto.domain.numbergenerator.RandomNumberGenerable;
import com.lotto.domain.numbergenerator.SixRandomNumbersDto;
import com.lotto.infrastructure.numbergenerator.http.RandomGeneratorClientConfigProperties;
import com.lotto.infrastructure.numbergenerator.http.RestTemplateResponseErrorHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.server.ResponseStatusException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class RandomNumberGeneratorRestTemplateIntegrationTest {

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    RandomNumberGenerable randomNumberGenerator = new RandomNumberGeneratorRestTemplateIntegrationTestConfig()
            .remoteNumberGeneratorClient(
                    new RandomGeneratorClientConfigProperties(
                            1000,
                            1000,
                            "http://localhost",
                            wireMockServer.getPort()
                    ),
                    new RestTemplateResponseErrorHandler()
            );

    @Test
    public void should_return_200_ok_and_six_numbers() {
        // given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [1, 2, 67, 23, 24, 25]
                                """.trim())
                ));

        // when
        SixRandomNumbersDto response = randomNumberGenerator.generateSixRandomNumbers(1, 99);

        // then
        assertThat(response.numbers()).containsExactly(1, 2, 67, 23, 24, 25);
    }

    @Test
    void should_return_null_numbers_when_fault_connection_reset_by_peer() {
        // given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        // then
        assertThatThrownBy(() -> randomNumberGenerator.generateSixRandomNumbers(1, 99))
                .hasMessageContaining("Connection reset");

    }

    @Test
    void should_throw_500_internal_server_error_when_server_returns_500() {
        // given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withHeader("Content-Type", "application/json")));

        // when & then
        assertThatThrownBy(() -> randomNumberGenerator.generateSixRandomNumbers(1, 99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("500 INTERNAL_SERVER_ERROR")
                .hasMessageContaining("Error while using http client");
    }

    @Test
    void should_throw_404_not_found_when_server_returns_404() {
        // given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
        .willReturn(WireMock.aResponse()
                .withStatus(HttpStatus.SC_NOT_FOUND)
                .withHeader("Content-Type", "application/json")
        ));

        // when & then
        assertThatThrownBy(() -> randomNumberGenerator.generateSixRandomNumbers(1, 99))
                .hasMessageContaining("404 NOT_FOUND");
    }

    @Test
    void should_throw_401_unauthorized_when_server_returns_401() {
        // given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_UNAUTHORIZED)
                        .withHeader("Content-Type", "application/json")));

        // when & then
        assertThatThrownBy(() -> randomNumberGenerator.generateSixRandomNumbers(1, 99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401 UNAUTHORIZED");
    }

    @Test
    void should_throw_500_internal_server_error_when_timeout_is_too_long() {
        // given
        wireMockServer.stubFor(WireMock.get("/api/v1.0/random?min=1&max=99&count=6")
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(5000)
                ));

        // when & then
        assertThatThrownBy(() -> randomNumberGenerator.generateSixRandomNumbers(1, 99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("500 INTERNAL_SERVER_ERROR");
    }
}
