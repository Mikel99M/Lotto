package com.lotto.domain.resultannouncer;

import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;
import com.lotto.domain.stub.NumberReceiverFacadeStub;
import com.lotto.domain.stub.ResultCheckerFacadeStub;
import com.lotto.domain.stub.WinningNumbersGeneratorFacadeStubForResultAnnouncer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultAnnouncerFacadeTest {

    ResultCheckerFacadeStub resultCheckerFacadeStub;
    NumberReceiverFacadeStub numberReceiverFacadeStub;
    WinningNumbersGeneratorFacadeStubForResultAnnouncer winningNumbersGeneratorFacadeStub;

    ZoneId zoneId = ZoneId.of("Europe/Warsaw");
    Clock clock = Clock.systemUTC();
    Set<Integer> numbers = new HashSet<>(Set.of(1, 2, 3, 4, 5, 6));
    ZonedDateTime fixedTime =
            ZonedDateTime.of(2025, 11, 8, 20, 0, 0, 0, zoneId);
    Instant drawDate = Instant.parse("2025-11-08T20:00:00Z");

    ResultAnnouncerFacade resultAnnouncerFacade;

    @BeforeEach
    public void setUp() {
        resultCheckerFacadeStub = new ResultCheckerFacadeStub();
        numberReceiverFacadeStub = new NumberReceiverFacadeStub();
        winningNumbersGeneratorFacadeStub = new WinningNumbersGeneratorFacadeStubForResultAnnouncer();

        ZonedDateTime fixedTime =
                ZonedDateTime.of(2025, 11, 9, 12, 0, 0, 0, zoneId);
        Clock clock = Clock.fixed(fixedTime.toInstant(), zoneId);

        resultAnnouncerFacade = new ResultAnnouncerFacade(resultCheckerFacadeStub, numberReceiverFacadeStub, winningNumbersGeneratorFacadeStub, clock);
    }

    @Test
    public void should_return_no_ticket_found_when_hash_does_not_exist() {
        // given
        ResultAnnouncerFacade resultCheckerFacade = new ResultAnnouncerFacade(resultCheckerFacadeStub, numberReceiverFacadeStub, winningNumbersGeneratorFacadeStub, clock);

        // when
        ResultAnnouncerResponseDto result = resultCheckerFacade.checkResult("test_hash");

        // then
        assertThat(result.message()).isEqualTo(ResponseMessages.NO_TICKET_WITH_THIS_HASH_FOUND.info);

    }

    @Test
    public void should_return_before_draw_date_message_when_ticket_draw_is_in_future() {
        // given
        clock = Clock.fixed(ZonedDateTime.of(2025, 11, 6, 12, 3, 2, 0, zoneId)
                        .toInstant(),
                zoneId);
        TicketDto ticketBeforeDrawDate = TicketDto.builder()
                .hash("test_hash2")
                .drawDate(drawDate)
                .numbers(numbers)
                .build();
        numberReceiverFacadeStub.addTicket(ticketBeforeDrawDate);
        ResultAnnouncerFacade resultCheckerFacade = new ResultAnnouncerFacade(resultCheckerFacadeStub, numberReceiverFacadeStub, winningNumbersGeneratorFacadeStub, clock);

        // when
        ResultAnnouncerResponseDto result = resultCheckerFacade.checkResult("test_hash2");

        // then
        assertThat(result.message()).isEqualTo(ResponseMessages.IT_IS_BEFORE_DRAW_DATE.info);

    }

    @Test
    public void should_return_ticket_has_won_when_ticket_is_winning() {
        // given
        clock = Clock.fixed(ZonedDateTime.of(2025, 11, 9, 12, 3, 2, 0, zoneId)
                        .toInstant(),
                zoneId);
        TicketDto winningTicket = TicketDto.builder()
                .hash("test_hash3")
                .drawDate(drawDate)
                .numbers(numbers)
                .build();
        numberReceiverFacadeStub.addTicket(winningTicket);
        resultCheckerFacadeStub.addTicketsThatWon(winningTicket);
        ResultAnnouncerFacade resultCheckerFacade = new ResultAnnouncerFacade(resultCheckerFacadeStub, numberReceiverFacadeStub, winningNumbersGeneratorFacadeStub, clock);

        // when
        ResultAnnouncerResponseDto result = resultCheckerFacade.checkResult("test_hash3");

        // then
        assertThat(result.message()).isEqualTo(ResponseMessages.TICKET_HAS_WON.info);

    }

    @Test
    public void should_return_ticket_has_lost_when_ticket_is_not_winning() {
        // given
        clock = Clock.fixed(ZonedDateTime.of(2025, 11, 9, 12, 3, 2, 0, zoneId)
                        .toInstant(),
                zoneId);
        TicketDto loosingTicket = TicketDto.builder()
                .hash("loosing_hash")
                .drawDate(drawDate)
                .numbers(numbers)
                .build();
        numberReceiverFacadeStub.addTicket(loosingTicket);

        TicketDto winningTicket = TicketDto.builder()
                .hash("test_hash4")
                .drawDate(drawDate)
                .numbers(numbers)
                .build();
        resultCheckerFacadeStub.addTicketsThatWon(winningTicket);

        ResultAnnouncerFacade resultCheckerFacade = new ResultAnnouncerFacade(resultCheckerFacadeStub, numberReceiverFacadeStub, winningNumbersGeneratorFacadeStub, clock);

        // when
        ResultAnnouncerResponseDto result = resultCheckerFacade.checkResult("loosing_hash");

        // then
        assertThat(result.message()).isEqualTo(ResponseMessages.TICKET_HAS_LOST.info);

    }

    @Test
    public void should_return_correct_response_structure_when_ticket_found() {
        // given
        TicketDto winningTicket = TicketDto.builder()
                .hash("test_hash5")
                .drawDate(drawDate)
                .numbers(numbers)
                .build();
        numberReceiverFacadeStub.addTicket(winningTicket);
        resultCheckerFacadeStub.addTicketsThatWon(winningTicket);

        winningNumbersGeneratorFacadeStub.addWinningNumbers(
                drawDate,
                numbers
        );

        // when
        ResultAnnouncerResponseDto result = resultAnnouncerFacade.checkResult("test_hash5");

        // then
        assertThat(result.message()).isEqualTo(ResponseMessages.TICKET_HAS_WON.info);
        assertThat(result.response().hash()).isEqualTo("test_hash5");
        assertThat(result.response().drawDate()).isEqualTo(drawDate);
        assertThat(result.response().numbers()).isEqualTo(numbers);
        assertThat(result.response().winningNumbers()).isEqualTo(numbers);
        assertThat(result.response().isWon()).isTrue();
    }

}
