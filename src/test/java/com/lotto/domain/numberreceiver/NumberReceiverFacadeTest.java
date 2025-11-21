package com.lotto.domain.numberreceiver;

import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.stub.HashGeneratorStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberReceiverFacadeTest {

    private TicketRepository ticketRepository;
    private NumberReceiverFacade facade;
    private HashGeneratorStub hashGenerator;
    private Clock clock;
    private NumberReceiverConfiguration numberReceiverConfiguration = new NumberReceiverConfiguration();

    @BeforeEach
    void setup() {
        hashGenerator = new HashGeneratorStub();
        ticketRepository = new TicketRepositoryImplementation();
        clock = Clock.systemUTC();
        facade = numberReceiverConfiguration.generateForTests(clock, ticketRepository, hashGenerator);
    }

    @Test
    public void should_return_correct_response_when_user_input_six_numbers_in_range() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isNotNull();
        assertThat(result.hash()).isNotNull();
    }

    @Test
    public void should_return_failed_message_when_user_input_six_numbers_but_one_number_is_out_of_range() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 100, 5, 6);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("failed");
        assertThat(result.drawDate()).isNull();
        assertThat(result.hash()).isNull();
    }

    @Test
    public void should_return_failed_message_when_user_input_six_numbers_but_one_number_is_out_of_range_and_is_negative() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, -50, 5, 6);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("failed");
        assertThat(result.drawDate()).isNull();
        assertThat(result.hash()).isNull();
    }

    @Test
    public void should_return_failed_message_when_user_input_less_than_six_numbers() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("failed");
        assertThat(result.drawDate()).isNull();
        assertThat(result.hash()).isNull();

    }

    @Test
    public void should_return_failed_message_when_user_input_more_than_six_numbers() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6, 7);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("failed");
        assertThat(result.drawDate()).isNull();
        assertThat(result.hash()).isNull();
    }

    @Test
    public void should_return_correct_hash() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result.hash()).isEqualTo("hash-test");

    }

    @Test
    public void should_return_correct_draw_date() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        LocalDateTime nextDrawDate = nextSaturday.withHour(20).withMinute(0).withSecond(0).withNano(0);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result.drawDate()).isEqualTo(nextDrawDate);
    }

    @Test
    public void should_return_next_Saturday_draw_date_when_date_is_Saturday_noon() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        clock = Clock.fixed(LocalDateTime.of(2025, 11, 8, 11, 5, 40).toInstant(ZoneOffset.UTC), ZoneId.of("Europe/London"));
        NumberReceiverFacade facade = numberReceiverConfiguration.generateForTests(clock, ticketRepository, hashGenerator);

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        LocalDateTime nextDrawDate = nextSaturday.withHour(20).withMinute(0).withSecond(0).withNano(0);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isNotNull();
        assertThat(result.hash()).isNotNull();
        assertThat(result.drawDate()).isEqualTo(nextDrawDate);
    }

    @Test
    public void should_return_next_Saturday_draw_date_when_date_is_Saturday_after_20() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        clock = Clock.fixed(LocalDateTime.of(2025, 11, 8, 20, 5, 10).toInstant(ZoneOffset.UTC), ZoneId.of("Europe/London"));
        NumberReceiverFacade facade = numberReceiverConfiguration.generateForTests(clock, ticketRepository, hashGenerator);

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        LocalDateTime nextDrawDate = nextSaturday.withHour(20).withMinute(0).withSecond(0).withNano(0);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isNotNull();
        assertThat(result.hash()).isNotNull();
        assertThat(result.drawDate()).isEqualTo(nextDrawDate);
    }

    @Test
    public void should_return_tickets_with_correct_draw_date() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        clock = Clock.fixed(LocalDateTime.of(2025, 11, 13, 10, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.of("Europe/London"));
        NumberReceiverFacade facade = numberReceiverConfiguration.generateForTests(clock, ticketRepository, hashGenerator);

        LocalDateTime drawDate = (LocalDateTime.of(2025, 11, 15, 20, 0, 0));

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        List<TicketDto> tickets = facade.fetchAllTicketDtos(drawDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(tickets.size()).isEqualTo(1);
        assertThat(tickets.get(0).drawDate()).isEqualTo(drawDate);

    }

    @Test
    public void should_return_empty_collections_if_there_are_no_tickets() {
        // given
        LocalDateTime drawDate = (LocalDateTime.of(2025, 11, 15, 20, 0, 0));

        // when
        List<TicketDto> tickets = facade.fetchAllTicketDtos(drawDate);

        // then
        assertThat(tickets.size()).isEqualTo(0);
    }

    @Test
    public void should_return_empty_collections_if_given_date_is_after_next_drawDate() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        clock = Clock.fixed(LocalDateTime.of(2025, 11, 13, 10, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.of("Europe/London"));
        NumberReceiverFacade facade = numberReceiverConfiguration.generateForTests(clock, ticketRepository, hashGenerator);

        LocalDateTime drawDate = (LocalDateTime.of(2025, 11, 16, 20, 0, 0));

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        List<TicketDto> tickets = facade.fetchAllTicketDtos(drawDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(tickets.size()).isEqualTo(0);
    }
}