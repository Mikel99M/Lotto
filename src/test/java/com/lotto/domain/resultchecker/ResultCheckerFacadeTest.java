package com.lotto.domain.resultchecker;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import com.lotto.domain.resultchecker.dto.ResultDto;
import com.lotto.domain.stub.NumberReceiverFacadeStub;
import com.lotto.domain.stub.WinningNumbersGeneratorFacadeStub;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
class ResultCheckerFacadeTest {

    private final ZoneId businessZone = ZoneId.of("Europe/Warsaw");

    private final Instant drawDate = ZonedDateTime.of(2025, 11, 15, 20, 0, 0, 0, businessZone).toInstant();
    private final Set<Integer> winningNumbers = Set.of(1, 2, 3, 4, 5, 6);
    private final Set<Integer> loosingNumbers1 = Set.of(1, 2, 3, 12, 5, 6);
    private final Set<Integer> loosingNumbers2 = Set.of(1, 9, 3, 4, 5, 8);


    private final WinningNumbersGeneratorFacadeStub winningNumbersGeneratorFacadeStub = new WinningNumbersGeneratorFacadeStub();
    private final NumberReceiverFacadeStub numberReceiverFacadeStub = new NumberReceiverFacadeStub();

    private final ResultCheckerFacade facade = new ResultCheckerFacade(
            winningNumbersGeneratorFacadeStub,
            numberReceiverFacadeStub
    );

    @Test
    public void should_return_correct_ResultDto_with_empty_list_when_there_are_no_tickets_at_all() {
        // when
        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(drawDate);
        assertThat(result.winningTickets()).isEmpty();
    }

    @Test
    public void should_return_correct_ResultDto_with_only_one_TicketDto_when_there_is_only_one_TicketDto() {
        // given
        winningNumbersGeneratorFacadeStub.addWinningNumbers(new WinningNumbersDto(winningNumbers, drawDate));

        // when
        TicketDto winningTicket = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build();

        numberReceiverFacadeStub.addTicket(winningTicket);

        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(drawDate);
        assertThat(result.winningTickets().size()).isEqualTo(1);
        assertThat(result.winningTickets().get(0)).isEqualTo(winningTicket);
    }

    @Test
    public void should_return_correct_ResultDto_with_only_one_TicketDto_when_there_are_many_TicketDtos() {
        // given
        winningNumbersGeneratorFacadeStub.addWinningNumbers(new WinningNumbersDto(winningNumbers, drawDate));

        // when
        TicketDto winningTicket = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto loosingTicket2 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(loosingNumbers2)
                .build();

        numberReceiverFacadeStub.addTicket(winningTicket);
        numberReceiverFacadeStub.addTicket(loosingTicket1);
        numberReceiverFacadeStub.addTicket(loosingTicket2);

        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(drawDate);
        assertThat(result.winningTickets()).hasSize(1);
        assertThat(result.winningTickets().get(0)).isEqualTo(winningTicket);
        assertThat(result.winningTickets().get(0)).isNotEqualTo(loosingTicket1);
        assertThat(result.winningTickets().get(0).numbers()).isEqualTo(winningNumbers);
        assertThat(result.winningTickets().get(0).drawDate()).isEqualTo(drawDate);
    }

    @Test
    public void should_return_correct_ResultDto_with_two_different_TicketDtos() {
        // given
        winningNumbersGeneratorFacadeStub.addWinningNumbers(new WinningNumbersDto(winningNumbers, drawDate));

        // when
        TicketDto winningTicket1 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto winningTicket2 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build();

        numberReceiverFacadeStub.addTicket(winningTicket1);
        numberReceiverFacadeStub.addTicket(loosingTicket1);
        numberReceiverFacadeStub.addTicket(winningTicket2);

        ResultDto result = facade.checkResult(drawDate);

        // then
        assertThat(result.drawDate()).isEqualTo(drawDate);
        assertThat(result.winningTickets().size()).isEqualTo(2);
        assertThat(result.winningTickets().get(0)).isEqualTo(winningTicket1);
        assertThat(result.winningTickets().get(1)).isEqualTo(winningTicket2);
        assertThat(result.winningTickets().get(0).numbers()).isEqualTo(winningNumbers);
        assertThat(result.winningTickets().get(0).numbers()).isEqualTo(result.winningTickets().get(1).numbers());
    }

    @Test
    public void should_return_empty_list_when_there_are_no_tickets_at_all() {
        // when
        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).isEmpty();
    }

    @Test
    public void should_return_empty_list_when_there_are_no_winning_tickets_on_the_draw_date() {
        // given
        numberReceiverFacadeStub.addTicket(TicketDto.builder()
                .drawDate(drawDate)
                .numbers(Set.of(1, 2, 3, 4, 5, 8))
                .build());

        // when
        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).isEmpty();

    }

    @Test
    public void should_return_list_with_one_ticket_when_there_is_only_one_ticket_at_all() {
        // given
        winningNumbersGeneratorFacadeStub.addWinningNumbers(new WinningNumbersDto(winningNumbers, drawDate));

        // when
        numberReceiverFacadeStub.addTicket(TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build());

        winningNumbersGeneratorFacadeStub.addWinningNumbers(WinningNumbersDto.builder()
                .date(drawDate)
                .winningNumbers(winningNumbers)
                .build());

        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).hasSize(1);
        assertThat(list.get(0).numbers()).isEqualTo(winningNumbers);
    }

    @Test
    public void should_return_list_with_one_ticket_when_there_is_only_one_ticket_that_won_out_of_many() {
        // given
        winningNumbersGeneratorFacadeStub.addWinningNumbers(new WinningNumbersDto(winningNumbers, drawDate));

        // when
        TicketDto winningTicket = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto loosingTicket2 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(loosingNumbers2)
                .build();

        numberReceiverFacadeStub.addTicket(winningTicket);
        numberReceiverFacadeStub.addTicket(loosingTicket1);
        numberReceiverFacadeStub.addTicket(loosingTicket2);

        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).hasSize(1);
        assertThat(list.get(0).numbers()).isEqualTo(winningNumbers);
    }

    @Test
    public void should_return_list_with_two_tickets_that_won() {
        // given
        winningNumbersGeneratorFacadeStub.addWinningNumbers(new WinningNumbersDto(winningNumbers, drawDate));

        // when
        TicketDto winningTicket1 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build();

        TicketDto loosingTicket1 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(loosingNumbers1)
                .build();

        TicketDto winningTicket2 = TicketDto.builder()
                .drawDate(drawDate)
                .numbers(winningNumbers)
                .build();

        numberReceiverFacadeStub.addTicket(winningTicket1);
        numberReceiverFacadeStub.addTicket(loosingTicket1);
        numberReceiverFacadeStub.addTicket(winningTicket2);

        List<TicketDto> list = facade.retrieveWinningTickets(drawDate);

        // then
        assertThat(list).hasSize(2);
        assertThat(list.get(0).numbers()).isEqualTo(list.get(1).numbers());
    }

    @Test
    public void findWinningTicketByHash_should_return_empty_optional_when_there_is_no_hash() {
        // when
        Optional<TicketDto> result = facade.findWinningTicketByHash("asda");

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void findWinningTicketByHash_should_return_correct_TicketDto() {
        // given
        winningNumbersGeneratorFacadeStub.addWinningNumbers(new WinningNumbersDto(winningNumbers, drawDate));

        numberReceiverFacadeStub.addTicket(
                TicketDto.builder()
                        .hash("hash")
                        .drawDate(drawDate)
                        .numbers(winningNumbers)
                        .build()
        );

        // when
        Optional<TicketDto> result = facade.findWinningTicketByHash("hash");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().numbers()).isEqualTo(winningNumbers);
        assertThat(result.get().hash()).isEqualTo("hash");
    }

}