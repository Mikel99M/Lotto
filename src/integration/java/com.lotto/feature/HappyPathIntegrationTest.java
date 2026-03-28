package com.lotto.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.jsonpath.JsonPath;
import com.lotto.BaseIntegrationTest;
import com.lotto.domain.AdjustableClock;
import com.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import com.lotto.domain.resultchecker.dto.ResultDto;
import com.lotto.infrastructure.resultchecker.scheduler.ResultCheckerScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HappyPathIntegrationTest extends BaseIntegrationTest {

    private final Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);
    @Autowired
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;
    @Autowired
    ResultCheckerScheduler resultCheckerScheduler;
    @Autowired
    Clock clock;

    private AdjustableClock adjustableClock() {
        return (AdjustableClock) clock;
    }

    @Test
    void testHappyScenarioWhereUserWins() throws Exception {

        //    step 1: external service returns 6 random numbers (1,2,3,4,5,6)
        // given
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/api/v1.0/random"))
                        .withQueryParam("min", equalTo("1"))
                        .withQueryParam("max", equalTo("99"))
                        .withQueryParam("count", equalTo("6"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                        [1, 2, 3, 4, 5, 6]
                                        """.trim()
                                )));

        // step 2: system fetched winning numbers for draw date:
        WinningNumbersDto winningNumbersDto =
                winningNumbersGeneratorFacade.generate();

        assertThat(winningNumbersDto.winningNumbers())
                .isEqualTo(winningNumbers);

        // step 3: /result/recent returns 404 and response: "No winning numbers found for draw date: 2025-12-13T19:00:00Z"
        mockMvc.perform(get("/result/recent"))
                .andExpect(status().isNotFound())
                .andExpect(
                        content().json(
                                """
                                                {
                                                  "response": "No winning numbers found for draw date: 2025-12-13T19:00:00Z",
                                                  "status": "NOT_FOUND"
                                                }
                                        """.trim()
                        )
                );

        // step 4: user made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) at 14-12-2025 10:00 and system returned OK(200) with message: “success” and Ticket (DrawDate:20.12.2025 20:00 (Saturday))
        // when & then
        ResultActions perform = mockMvc.perform(post("/inputNumbers")
                        .content("""
                                {
                                "inputNumbers": [1,2,3,4,5,6]
                                }
                                """.trim()
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.drawDate").value("2025-12-20T20:00:00+01:00"))
                .andExpect(jsonPath("$.numbersFromUser").isArray())
                .andExpect(jsonPath("$.numbersFromUser", hasItems(1, 2, 3, 4, 5, 6)))
                .andExpect(jsonPath("$.hash").exists());

        String json = perform.andReturn().getResponse().getContentAsString();
        String hash = JsonPath.read(json, "$.hash");

        //    step 5: user made GET /result/notExistingId and system returned 200 and body with message "No ticket with this hash found"
        // when
        ResultActions performGetResultsWithNotExistingId = mockMvc.perform(get("/result/" + "nonExistingId"));

        performGetResultsWithNotExistingId.andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                {
                                  "response": {
                                    "hash": null,
                                    "numbers": null,
                                    "drawDate": null,
                                    "isWon": false
                                  },
                                  "message": "No ticket with this hash found"
                                }
                                """.trim()
                ));

        //    step 6: user made GET /result/hash and system returned 200 and body with message It is before draw date
        // when & then
        mockMvc.perform(get("/result/" + hash)).andExpect(
                content().json(
                        """
                                {
                                  "response": {
                                    "hash": %s,
                                    "numbers": [1, 2, 3, 4, 5, 6],
                                    "winningNumbers": %s,
                                    "drawDate": "2025-12-20T19:00:00Z",
                                    "isWon": false
                                  },
                                  "message": "It is before draw date"
                                }
                                """.trim().formatted(hash, winningNumbers)
                )
        );

        //    step 7: 6 days and 31 minutes passed, and it is 1 minute after the draw date (20.12.2025 20:01)
        // given
        adjustableClock().plusDaysAndMinutes(6, 31);

        // when & then
        mockMvc.perform(get("/result/" + hash)).andExpect(
                content().json(
                        """
                                {
                                  "response": {
                                    "hash": "%s",
                                    "numbers": [1, 2, 3, 4, 5, 6],
                                    "winningNumbers": [1, 2, 3, 4, 5, 6],
                                    "drawDate": "2025-12-20T19:00:00Z",
                                    "isWon": true
                                  },
                                  "message": "Ticket has won"
                                }
                                """.trim().formatted(hash)
                )
        );

        // step 8: /result/recent returns now body with recent winning numbers
        mockMvc.perform(get("/result/recent"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json(
                                """
                                        {
                                          "winningNumbers": [1, 2, 3, 4, 5, 6],
                                          "date": "2025-12-20T19:00:00Z"
                                        }
                                        """.trim()
                        )
                );

        // step 9: resultCheckerScheduler generated ResultDto with winning tickets
        // given & when
        ResultDto result = resultCheckerScheduler.run();

        // then
        assertAll(
                () -> assertThat(result.winningTickets()).hasSize(1),
                () -> {
                    assert result.winningTickets() != null;
                    assertThat(result.winningTickets().get(0).numbers()).isEqualTo(winningNumbers);
                },
                () -> {
                    assert result.winningTickets() != null;
                    assertThat(result.winningTickets().get(0).purchaseDate()).isEqualTo(Instant.parse("2025-12-14T18:30:00Z"));
                },
                () -> assertThat(result.drawDate()).isEqualTo(Instant.parse("2025-12-20T19:00:00Z"))
        );
    }
}
