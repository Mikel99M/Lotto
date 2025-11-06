//package com.lotto.domain.numberreceiver;
//
//import com.lotto.domain.AdjustableClock;
//import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
//import com.lotto.domain.numberreceiver.dto.TicketDto;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZoneOffset;
//import java.util.List;
//import java.util.Set;
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class NumberReceiverFacadeTest {
//
//    AdjustableClock clock = new AdjustableClock(LocalDateTime.of(2023, 2, 15, 11, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
//
//    NumberReceiverFacade facade = new NumberReceiverFacade(
//            new NumberValidator(),
//            new InMemoryNumberReceiverRepositoryTest(),
//            clock
//    );
//
//
//    @Test
//    public void should_return_success_when_user_gave_six_numbers() {
//        //when
//        InputNumberResultDto result = facade.inputNumbers(Set.of(1,2,3,4,5,6));
//        //then
//        assertThat(result.message()).isEqualTo("success");
//    }
//
//    @Test
//    public void should_return_failed_when_user_gave_less_than_six_numbers() {
//        //when
//        InputNumberResultDto result = facade.inputNumbers(Set.of(1,2,3,4,5));
//        //then
//        assertThat(result.message()).isEqualTo("failed");
//    }
//
//    @Test
//    public void should_return_failed_when_user_gave_more_than_six_numbers() {
//        //when
//        InputNumberResultDto result = facade.inputNumbers(Set.of(1,2,3,4,5,6,7));
//        //then
//        assertThat(result.message()).isEqualTo("failed");
//    }
//
//    @Test
//    public void should_return_failed_when_user_gave_at_least_one_number_out_of_range_of_1_to_99() {
//        //when
//        InputNumberResultDto result = facade.inputNumbers(Set.of(1,1231,3,4,5,6));
//        //then
//        assertThat(result.message()).isEqualTo("failed");
//    }
//
//    @Test
//    public void should_return_save_to_database_when_user_gave_six_numbers() {
//        // given
//        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);
//        InputNumberResultDto result = facade.inputNumbers(numbersFromUser);
//        LocalDateTime drawDate = LocalDateTime.of(2023, 2, 15, 12, 0, 0);
//        // when
//        List<TicketDto> ticketDtos = facade.userNumbers(drawDate);
//        // then
//        assertThat(ticketDtos).contains(
//                TicketDto.builder()
//                        .hash(result.hash())
//                        .drawDate(drawDate)
//                        .numbersFromUser(result.numbersFromUser())
//                        .build()
//        );
//
//        System.out.println("Expected: " + TicketDto.builder()
//                .hash(result.hash())
//                .drawDate(drawDate)
//                .numbersFromUser(result.numbersFromUser())
//                .build());
//
//    }
//
//}
//

package com.lotto.domain.numberreceiver;

import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberReceiverFacadeTest {

    Clock clock = Clock.systemUTC();
    LocalDateTime now = LocalDateTime.now(clock);

    private TicketRepository ticketRepository = new TicketRepositoryImplementation();

    NumberReceiverFacade facade = new NumberReceiverFacade(
            new NumberValidator(),
            ticketRepository,
            clock,
            new HashGenerator()
    );

    @Test
    public void should_return_correct_response_when_user_input_six_numbers_in_range() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isNotNull();
        assertThat(result.hash()).isNotNull();

    }

    @Test
    public void it_should_return_failed_message_when_user_input_six_numbers_but_one_number_is_out_of_range() {
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
    public void it_should_return_failed_message_when_user_input_six_numbers_but_one_number_is_out_of_range_and_is_negative() {
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
    public void it_should_return_failed_message_when_user_input_less_than_six_numbers() {
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
    public void it_should_return_failed_message_when_user_input_more_than_six_numbers() {
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
    public void it_should_return_correct_hash() {
        // given

    }

    @Test
    public void it_should_return_correct_draw_date() {
        // given

    }

    @Test
    public void it_should_return_next_Saturday_draw_date_when_date_is_Saturday_noon() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isNotNull();
        assertThat(result.hash()).isNotNull();
        System.out.println(result.drawDate());

    }

    @Test
    public void it_should_return_next_Saturday_draw_date_when_date_is_Saturday_after_20() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        clock = Clock.fixed(LocalDateTime.of(2025, 11, 8, 20, 5, 0).toInstant(ZoneOffset.UTC), ZoneId.of("Europe/London"));
        NumberReceiverFacade facade = new NumberReceiverFacade(
                new NumberValidator(),
                ticketRepository,
                clock,
                new HashGenerator()
        );

        LocalDateTime expectedDate = (LocalDateTime.of(2025, 11, 15, 20, 0, 0));

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isEqualTo(expectedDate);
        assertThat(result.hash()).isNotNull();

    }

    @Test
    public void it_should_return_tickets_with_correct_draw_date() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        clock = Clock.fixed(LocalDateTime.of(2025, 11, 13, 10, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.of("Europe/London"));
        NumberReceiverFacade facade = new NumberReceiverFacade(
                new NumberValidator(),
                ticketRepository,
                clock,
                new HashGenerator()
        );

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
    public void it_should_return_empty_collections_if_there_are_no_tickets() {
        // given
        LocalDateTime drawDate = (LocalDateTime.of(2025, 11, 15, 20, 0, 0));

        // when
        List<TicketDto> tickets = facade.fetchAllTicketDtos(drawDate);

        // then
        assertThat(tickets.size()).isEqualTo(0);
    }

    @Test
    public void it_should_return_empty_collections_if_given_date_is_after_next_drawDate() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);

        clock = Clock.fixed(LocalDateTime.of(2025, 11, 13, 10, 0, 0).toInstant(ZoneOffset.UTC), ZoneId.of("Europe/London"));
        NumberReceiverFacade facade = new NumberReceiverFacade(
                new NumberValidator(),
                ticketRepository,
                clock,
                new HashGenerator()
        );

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

    @Test
    public void it_should_return_next_draw_date() {
        // given
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 6);
        LocalDateTime nextDrawDate = DrawDateGenerator.generateNextDrawDate(now);

        // when
        InputNumberResultDto result = facade.inputNumbers(numbers);

        // then
        assertThat(result).isNotNull();
        assertThat(result.message()).isEqualTo("success");
        assertThat(result.numbersFromUser()).isEqualTo(numbers);
        assertThat(result.drawDate()).isEqualTo(nextDrawDate);
        assertThat(result.hash()).isNotNull();
    }

}