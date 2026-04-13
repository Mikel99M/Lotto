package com.lotto.domain.numbergenerator;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import com.lotto.domain.stub.DrawDateGeneratorStub;
import com.lotto.domain.stub.NumbersGeneratorStub;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WinningNumbersGeneratorFacadeTest {

    ZoneId zoneId = ZoneId.of("Europe/Warsaw");
    ZonedDateTime fixedTime =
            ZonedDateTime.of(2025, 11, 9, 0, 0, 0, 0, zoneId);
    Clock clock = Clock.fixed(fixedTime.toInstant(), zoneId);

    WinningNumbersRepositoryImplementation repository = new WinningNumbersRepositoryImplementation();
    NumbersGeneratorStub numbersGeneratorStub = new NumbersGeneratorStub();
    DrawDateGeneratorStub drawDateGeneratorStub = new DrawDateGeneratorStub(zoneId);
    WinningNumbersMapper winningNumbersMapper = new WinningNumbersMapper();

    WinningNumbersGeneratorFacadeConfigurationProperties properties = WinningNumbersGeneratorFacadeConfigurationProperties.builder()
            .lowerBand(1)
                .upperBand(99)
                .build();

    WinningNumbersGeneratorFacade facade = new WinningNumbersGeneratorFacade(repository, numbersGeneratorStub, winningNumbersMapper, drawDateGeneratorStub, properties, clock, zoneId);

    @Test
    public void should_return_correct_result() {
        // when
        WinningNumbersDto dto = facade.generate();

        WinningNumbersDto expectedResult =
                new WinningNumbersDto(
                        Set.of(1, 2, 3, 4, 5, 6),
                        drawDateGeneratorStub.generateNextDrawDate(clock.instant())
                );

        Instant expectedDate =
                drawDateGeneratorStub.generateNextDrawDate(clock.instant());

        // then
        assertThat(dto).isEqualTo(expectedResult);
        assertThat(dto.date()).isEqualTo(expectedDate);
    }

    @Test
    public void should_throw_exception_when_no_winning_numbers_exist() {
        // given
        ZonedDateTime fixedTime =
                ZonedDateTime.of(2025, 11, 2, 20, 0, 0, 0, zoneId);
        Instant missingDateInstant = fixedTime.toInstant();

        // expect
        assertThrows(
                WinningNumbersNotFoundException.class,
                () -> facade.retrieveWinningNumbersDtoByDraw(missingDateInstant)
        );
    }

    @Test
    public void should_throw_exception_when_no_winning_numbers_for_requested_date() {
        // given
        ZonedDateTime fixedTime =
                ZonedDateTime.of(2025, 11, 2, 20, 0, 0, 0, zoneId);
        Instant missingDateInstant = fixedTime.toInstant();

        // when
        ZonedDateTime dateTime = ZonedDateTime.of(2019, 1, 12, 10, 0, 0, 0, zoneId);
        drawDateGeneratorStub.generateNextDrawDate(dateTime.toInstant());

        // then
        assertThrows(
                WinningNumbersNotFoundException.class,
                () -> facade.retrieveWinningNumbersDtoByDraw(missingDateInstant)
        );
    }

    @Test
    public void should_return_correct_winning_numbers_for_existing_draw_date() {
        // given
        WinningNumbersDto created = facade.generate();

        WinningNumbersDto expectedResult =
                new WinningNumbersDto(
                        Set.of(1, 2, 3, 4, 5, 6),
                        drawDateGeneratorStub.generateNextDrawDate(clock.instant())
                );

        Instant expectedDate = drawDateGeneratorStub.generateNextDrawDate(clock.instant());

        // when
        WinningNumbersDto retrieved = facade.retrieveWinningNumbersDtoByDraw(created.date());

        // then
        assertThat(retrieved).isEqualTo(expectedResult);
        assertThat(retrieved.date()).isEqualTo(expectedDate);
    }

    @Test
    public void retrieveMostRecentWinningNumbersDto_should_return_correct_result() {
        // given
        ZonedDateTime drawZoned =
                ZonedDateTime.of(2026, 2, 28, 20, 0, 0, 0, zoneId);
        Instant drawDate = drawZoned.toInstant();
        drawDateGeneratorStub.setNextDrawDate(drawDate);

        facade.generate();

        WinningNumbersDto mostRecentWinningNumbers =
                new WinningNumbersDto(
                        Set.of(1, 2, 3, 4, 5, 6),
                        drawDate
                );

        // when
        WinningNumbersDto result = facade.retrieveMostRecentWinningNumbersDto();

        // then
        assertThat(result).isEqualTo(mostRecentWinningNumbers);
        assertThat(result.winningNumbers()).containsExactlyInAnyOrder(1,2,3,4,5,6);
        assertThat(result.date()).isEqualTo(drawDate);
    }

    @Test
    public void retrieveAllWinningNumbersDtos_should_return_all_winning_numbers() {
        // given
        WinningNumbersDto winningNumbers1 = facade.generate();
        ZonedDateTime fixedTime =
                ZonedDateTime.of(2026, 2, 26, 0, 0, 0, 0, zoneId);
        drawDateGeneratorStub.setNextDrawDate(fixedTime.toInstant());
        WinningNumbersDto winningNumbers2 = facade.generate();
        fixedTime =
                ZonedDateTime.of(2026, 2, 25, 0, 0, 0, 0, zoneId);
        drawDateGeneratorStub.setNextDrawDate(fixedTime.toInstant());        WinningNumbersDto winningNumbers3 = facade.generate();
        fixedTime =
                ZonedDateTime.of(2026, 2, 15, 0, 0, 0, 0, zoneId);
        drawDateGeneratorStub.setNextDrawDate(fixedTime.toInstant());        WinningNumbersDto winningNumbers4 = facade.generate();

        // when
        List<WinningNumbersDto> result = facade.retrieveAllWinningNumbersDtos();

        // then
        assertThat(result).containsExactly(winningNumbers1, winningNumbers2, winningNumbers3, winningNumbers4);
        assertThat(result).hasSize(4);
        assertThat(result.get(0)).isEqualTo(winningNumbers1);
        assertThat(result.get(3)).isEqualTo(winningNumbers4);
    }

    @Test
    public void retrieveAllWinningNumbersDtos_should_return_empty_list_when_There_is_no_winning_numbers() {
        // when
        List<WinningNumbersDto> result = facade.retrieveAllWinningNumbersDtos();

        // then
        assertThat(result).hasSize(0);
        assertThat(result).isEmpty();
    }


}
