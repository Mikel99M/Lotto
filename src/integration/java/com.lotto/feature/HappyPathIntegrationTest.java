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
import java.util.regex.Pattern;

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
        WinningNumbersDto winningNumbersDto = winningNumbersGeneratorFacade.generate();

        assertThat(winningNumbersDto.winningNumbers()).isEqualTo(winningNumbers);

        //step 3: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned UNAUTHORIZED(401)
        // given & when
        ResultActions failedLoginRequest = mockMvc.perform(post("/token")
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        failedLoginRequest.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(content().json("""
                        {
                        "message": "Bad credentials",
                        "status": "UNAUTHORIZED"
                        }
                        """.trim()));

        //step 4: user made GET /result/recent with no jwt token and system returned UNAUTHORIZED(401)
        // given & when
        ResultActions failedGetOffersRequest = mockMvc.perform(get("/result/recent")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        failedGetOffersRequest.andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        //step 5: user made POST /register with username=someUser, password=somePassword and system registered user with status CREATED(201)
        // when & then
        mockMvc.perform(post("/register")
                        .content("""
                                {
                                "username": "someUser",
                                "password": "somePassword"
                                }
                                """.trim())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("someUser"))
                .andExpect(jsonPath("$.created").value(true))
                .andExpect(jsonPath("$.id").exists());

        //step 6: user tried to get JWT token by requesting POST /token with username=someUser, password=somePassword and system returned OK(200) and jwttoken=AAAA.BBBB.CCC
        // given & when
        ResultActions successLoginRequest = mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "username": "someUser",
                        "password": "somePassword"
                        }
                        """.trim())
        );

        String loginResponseJson = successLoginRequest
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("someUser"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(loginResponseJson, "$.token");

        assertThat(token).matches(Pattern.compile("^([A-Za-z0-9-_=]+\\.)+([A-Za-z0-9-_=])+\\.?$"));

        // step 7: /result/recent with header “Authorization: Bearer AAAA.BBBB.CCC” returns 404 and response: "No winning numbers found for draw date: 2025-12-13T19:00:00Z"
        performGetActionWithToken("/result/recent", token)
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

        // step 8: user made POST /inputNumbers with 6 numbers (1, 2, 3, 4, 5, 6) at 14-12-2025 10:00 and system returned OK(200) with message: “success” and Ticket (DrawDate:20.12.2025 20:00 (Saturday))
        // when & then
        ResultActions perform = mockMvc.perform(post("/inputNumbers")
                        .header("Authorization", "Bearer " + token)
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

        //    step 9: user made GET /result/notExistingId and system returned 200 and body with message "No ticket with this hash found"
        // when
        ResultActions performGetResultsWithNotExistingId =
                performGetActionWithToken("/result/" + "nonExistingId", token);

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

        //    step 10: user made GET /result/hash and system returned 200 and body with message It is before draw date
        // when & then
        performGetActionWithToken("/result/" + hash, token)
                .andExpect(
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

        //    step 11: 6 days and 31 minutes passed, and it is 1 minute after the draw date (20.12.2025 20:01)
        // given
        adjustableClock().plusDaysAndMinutes(6, 31);

        // when & then
        performGetActionWithToken("/result/" + hash, token)
                .andExpect(
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

        // step 12: /result/recent returns now body with recent winning numbers
        performGetActionWithToken("/result/recent", token)
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

        // step 13: resultCheckerScheduler generated ResultDto with winning tickets
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
