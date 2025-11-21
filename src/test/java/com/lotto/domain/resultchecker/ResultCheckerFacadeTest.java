package com.lotto.domain.resultchecker;

import com.lotto.domain.general.NumberReceiver;
import com.lotto.domain.general.WinningNumbersGenerator;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultchecker.dto.ResultDto;
import com.lotto.domain.stub.NumberReceiverFacadeStub;
import com.lotto.domain.stub.WinningNumbersGeneratorFacadeStub;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
class ResultCheckerFacadeTest {

    private final ResultCheckerConfiguration resultCheckerConfiguration = new ResultCheckerConfiguration();

    @Test
    public void should_return_correct_ResultDto_with_empty_list_when_there_are_no_tickets_at_all() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(exactDrawDate);
        assertThat(result.winningTickets()).isEmpty();
    }

    @Test
    public void should_return_correct_ResultDto_with_only_one_TicketDto_when_there_is_only_one_TicketDto() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        TicketDto winningTicket = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build();

        numberReceiverFacade.addTicket(winningTicket);

        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(exactDrawDate);
        assertThat(result.winningTickets().size()).isEqualTo(1);
        assertThat(result.winningTickets().get(0)).isEqualTo(winningTicket);
    }

    @Test
    public void should_return_correct_ResultDto_with_only_one_TicketDto_when_there_are_many_TicketDtos() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);
        Set<Integer> loosingNumbers1 = Set.of(1, 2, 3, 12, 5, 6);
        Set<Integer> loosingNumbers2 = Set.of(1, 9, 3, 4, 5, 8);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        TicketDto winningTicket = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto loosingTicket2 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(loosingNumbers2)
                .build();

        numberReceiverFacade.addTicket(winningTicket);
        numberReceiverFacade.addTicket(loosingTicket1);
        numberReceiverFacade.addTicket(loosingTicket2);

        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(exactDrawDate);
        assertThat(result.winningTickets().size()).isEqualTo(1);
        assertThat(result.winningTickets().get(0)).isEqualTo(winningTicket);
        assertThat(result.winningTickets().get(0)).isNotEqualTo(loosingTicket1);
    }

    @Test
    public void should_return_correct_ResultDto_with_two_different_TicketDtos() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);
        Set<Integer> loosingNumbers1 = Set.of(1, 2, 3, 12, 5, 6);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        TicketDto winningTicket1 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto winningTicket2 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build();

        numberReceiverFacade.addTicket(winningTicket1);
        numberReceiverFacade.addTicket(loosingTicket1);
        numberReceiverFacade.addTicket(winningTicket2);

        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(exactDrawDate);
        assertThat(result.winningTickets().size()).isEqualTo(2);
        assertThat(result.winningTickets().get(0)).isEqualTo(winningTicket1);
        assertThat(result.winningTickets().get(1)).isEqualTo(winningTicket2);
        assertThat(result.winningTickets().get(0).numbers()).isEqualTo(winningNumbers);
        assertThat(result.winningTickets().get(0).numbers()).isEqualTo(result.winningTickets().get(1).numbers());
    }

    @Test
    public void should_return_empty_list_when_there_are_no_tickets_at_all() {
        // given
        LocalDate drawDate = LocalDate.of(2025, 11, 15);

        NumberReceiver numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGenerator winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).isEmpty();
    }

    @Test
    public void should_return_empty_list_when_there_are_no_winning_tickets_on_the_draw_date() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        numberReceiverFacade.addTicket(TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(Set.of(1, 2, 3, 4, 5, 8))
                .build());

        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).isEmpty();

    }

    @Test
    public void should_return_list_with_one_ticket_when_there_is_only_one_ticket_at_all() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        numberReceiverFacade.addTicket(TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build());

        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).numbers()).isEqualTo(winningNumbers);
    }

    @Test
    public void should_return_list_with_one_ticket_when_there_is_only_one_ticket_that_won_out_of_many() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);
        Set<Integer> loosingNumbers1 = Set.of(1, 2, 3, 12, 5, 6);
        Set<Integer> loosingNumbers2 = Set.of(1, 9, 3, 4, 5, 8);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        TicketDto winningTicket = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto loosingTicket2 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(loosingNumbers2)
                .build();

        numberReceiverFacade.addTicket(winningTicket);
        numberReceiverFacade.addTicket(loosingTicket1);
        numberReceiverFacade.addTicket(loosingTicket2);

        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).size().isEqualTo(1);
        assertThat(list.get(0).numbers()).isEqualTo(winningNumbers);
    }

    @Test
    public void should_return_list_with_two_tickets_that_won() {
        // given
        LocalDateTime exactDrawDate = LocalDateTime.of(2025, 11, 15, 20, 00);
        LocalDate drawDate = LocalDate.of(2025, 11, 15);
        Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);
        Set<Integer> loosingNumbers1 = Set.of(1, 2, 3, 12, 5, 6);

        NumberReceiverFacadeStub numberReceiverFacade = new NumberReceiverFacadeStub();
        WinningNumbersGeneratorFacadeStub winningNumbersGenerator = new WinningNumbersGeneratorFacadeStub();
        ResultCheckerFacade facade = resultCheckerConfiguration.createForTests(numberReceiverFacade, winningNumbersGenerator);

        // when
        TicketDto winningTicket1 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto winningTicket2 = TicketDto.builder()
                .drawDate(exactDrawDate)
                .numbers(winningNumbers)
                .build();

        numberReceiverFacade.addTicket(winningTicket1);
        numberReceiverFacade.addTicket(loosingTicket1);
        numberReceiverFacade.addTicket(winningTicket2);

        winningNumbersGenerator.addWinningNumbers(WinningNumbersDto.builder()
                .date(exactDrawDate)
                .winningNumbers(winningNumbers)
                .build());

        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).size().isEqualTo(2);
        assertThat(list.get(0).numbers()).isEqualTo(list.get(1).numbers());
    }

}