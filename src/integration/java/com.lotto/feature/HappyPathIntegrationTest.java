package com.lotto.feature;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.lotto.BaseIntegrationTest;
import com.lotto.domain.AdjustableClock;
import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.numbergenerator.WinningNumbersGeneratorFacade;
import com.lotto.domain.numbergenerator.WinningNumbersNotFoundException;
import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.infrastructure.numbergenerator.scheduler.WinningNumbersScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class HappyPathIntegrationTest extends BaseIntegrationTest {

    @Autowired
    WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;

    @Autowired
    WinningNumbersScheduler scheduler;

    @Autowired
    DrawDateGenerator dateGenerator;

    @Autowired
    Clock clock;

    private AdjustableClock adjustableClock() {
        return (AdjustableClock) clock;
    }

    @Test
    void f() throws Exception {
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
        LocalDateTime drawDate = LocalDateTime.of(2025, 12, 20, 20, 0, 0);
        // given
        await()
                .atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(1))
                .until(
                        () -> {
                            try {
                                return !scheduler.generateWinningNumbers().winningNumbers().isEmpty();
                            } catch (WinningNumbersNotFoundException e) {
                                return false;
                            }
                        }
                );


        //    step 3: user made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) at 16-11-2022 10:00 and system returned OK(200) with message: “success” and Ticket (DrawDate:19.11.2022 12:00 (Saturday), TicketId: sampleTicketId)
        // given
        // when
        ResultActions perform = mockMvc.perform(post("/inputNumbers")
                .content("""
                        {
                        "inputNumbers": [1,2,3,4,5,6]
                        }
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON)
        );
        // then
        LocalDateTime now = LocalDateTime.now(clock);
        MvcResult mvcResult = perform.andExpect(status().isOk()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        InputNumberResultDto inputNumberResultDto = objectMapper.readValue(json, InputNumberResultDto.class);
        String hash = inputNumberResultDto.hash();
        assertAll(
                () -> assertThat(inputNumberResultDto.drawDate()).isEqualTo(drawDate),
                () -> assertThat(inputNumberResultDto.numbersFromUser()).isEqualTo(Set.of(1, 2, 3, 4, 5, 6)),
                () -> assertThat(inputNumberResultDto.operationDate()).isEqualTo(now),
                () -> assertThat(inputNumberResultDto.message()).isEqualTo("success")
        );

        //    step 4: user made GET /result/notExistingId and system returned 404 and body with
        // when
        ResultActions performGetResultsWithNotExistingId = mockMvc.perform(get("/results/" + "noxExistingId"));

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



        //    step 5: 3 days and 1 minute passed, and it is 1 minute after the draw date (19.11.2022 12:01)
        //    step 6: system generated result for TicketId: sampleTicketId with draw date 19.11.2022 12:00, and saved it with 6 hits
        //    step 7: 3 hours passed, and it is 1 minute after announcement time (19.11.2022 15:01)
        //    step 8: user made GET /results/sampleTicketId and system returned 200 (OK)
    }
}
