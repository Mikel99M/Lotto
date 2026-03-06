package com.lotto.domain.numberreceiver;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.stub.HashGeneratorStub;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberReceiverFacadeTest {

    private final TicketRepository ticketRepository = new TicketRepositoryImplementation();
    private final HashGeneratorStub hashGenerator = new HashGeneratorStub();

    private final ZoneId businessZone = ZoneId.of("Europe/Warsaw");
    private final Instant fixedDate = ZonedDateTime.of(2025, 11, 5, 15, 30, 0, 0, businessZone).toInstant();
    private Clock clock = Clock.fixed(fixedDate, businessZone);

    private final DrawDateGenerator dateGenerator = new DrawDateGenerator(businessZone);

    private final NumberReceiverFacade facade = new NumberReceiverFacade(
            new NumberValidator(),
            ticketRepository,
            hashGenerator,
            dateGenerator,
            clock,
            businessZone
    );

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

        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        ZonedDateTime nextDrawDate = nextSaturday.withHour(20).withMinute(0).withSecond(0).withNano(0);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result.drawDate()).isEqualTo(nextDrawDate);
    }

    @Test
    public void should_return_next_Saturday_draw_date_when_date_is_Saturday_noon() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        ZonedDateTime fixedTime =
                ZonedDateTime.of(2025, 11, 8, 11, 5, 40, 0, businessZone);

        Clock clock = Clock.fixed(fixedTime.toInstant(), businessZone);

        NumberReceiverFacade facade =
                new NumberReceiverFacade(new NumberValidator(), ticketRepository, hashGenerator, dateGenerator, clock, businessZone);

        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime expectedDrawDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .withHour(20).withMinute(0).withSecond(0).withNano(0);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isNotNull();
        assertThat(result.hash()).isNotNull();
        assertThat(result.drawDate()).isEqualTo(expectedDrawDate);
    }

    @Test
    public void should_return_next_Saturday_draw_date_when_date_is_Saturday_after_20() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        ZonedDateTime fixedTime =
                ZonedDateTime.of(2025, 11, 8, 20, 5, 10, 0, businessZone);
        clock = Clock.fixed(fixedTime.toInstant(), businessZone);

        NumberReceiverFacade facade = new NumberReceiverFacade(
                new NumberValidator(),
                ticketRepository,
                hashGenerator,
                dateGenerator,
                clock,
                businessZone);

        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        ZonedDateTime nextDrawDate = nextSaturday.withHour(20).withMinute(0).withSecond(0).withNano(0);

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
        Instant fixedDate = ZonedDateTime.of(2025, 11, 13, 10, 0, 0, 0, businessZone).toInstant();
        clock = Clock.fixed(fixedDate, businessZone);

        NumberReceiverFacade facade = new NumberReceiverFacade(
                new NumberValidator(),
                ticketRepository,
                hashGenerator,
                dateGenerator,
                clock,
                businessZone);

        ZonedDateTime drawDate = ZonedDateTime.of(2025, 11, 15, 20, 0, 0, 0, businessZone);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        List<TicketDto> tickets = facade.fetchAllTicketDtos(drawDate.toInstant());

        // then
        assertThat(result).isNotNull();
        assertThat(result.drawDate()).isEqualTo(drawDate);
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(tickets.size()).isEqualTo(1);
        assertThat(tickets.get(0).drawDate()).isEqualTo(drawDate.toInstant());

    }

    @Test
    public void should_return_empty_collections_if_there_are_no_tickets() {
        // given
        Instant drawDate = ZonedDateTime.of(2025, 11, 15, 20, 0, 0, 0, businessZone).toInstant();

        // when
        List<TicketDto> tickets = facade.fetchAllTicketDtos(drawDate);

        // then
        assertThat(tickets.size()).isEqualTo(0);
    }

    @Test
    public void should_return_empty_collections_if_given_date_is_after_next_drawDate() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        Instant fixedDate = ZonedDateTime.of(2025, 11, 13, 10, 0, 0, 0, businessZone).toInstant();

        clock = Clock.fixed(fixedDate, businessZone);
        NumberReceiverFacade facade = new NumberReceiverFacade(
                new NumberValidator(),
                ticketRepository,
                hashGenerator,
                dateGenerator,
                clock,
                businessZone);

        Instant drawDate = ZonedDateTime.of(2025, 11, 16, 20, 0, 0, 0, businessZone).toInstant();

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        List<TicketDto> tickets = facade.fetchAllTicketDtos(drawDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(tickets.size()).isEqualTo(0);
    }

    @Test
    public void fetchTicketByHash_should_return_correct_TickeDto() {
        // given
        Ticket ticket = new Ticket("test hash", Instant.now(), Instant.now(), Set.of());
        ticketRepository.save(ticket);

        // when
        TicketDto result = facade.fetchTicketByHash("test hash");

        // then
        assertThat(result).isNotNull();
        assertThat(result.hash()).isEqualTo("test hash");
    }

    @Test
    public void fetchTicketByHash_should_throw_ticketNotFoundException_when_it_doesnt_exist() {
        // then
        assertThrows(
                TicketNotFoundException.class,
                () -> facade.fetchTicketByHash("test hash")
        );
    }


}