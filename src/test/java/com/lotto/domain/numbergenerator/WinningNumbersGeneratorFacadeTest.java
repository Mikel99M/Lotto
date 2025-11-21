package com.lotto.domain.numbergenerator;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import com.lotto.domain.stub.DrawDateGeneratorStub;
import com.lotto.domain.stub.NumbersGeneratorStub;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WinningNumbersGeneratorFacadeTest {

    WinningNumbersRepositoryImplementation repository = new WinningNumbersRepositoryImplementation();
    NumbersGeneratorStub numbersGenerator = new NumbersGeneratorStub();
    DrawDateGeneratorStub drawDateGeneratorStub = new DrawDateGeneratorStub();

    WinningNumbersGeneratorFacade facade =
            new WinningNumbersConfiguration().createForTest(repository, numbersGenerator, drawDateGeneratorStub);

    LocalDateTime currentDateTime = LocalDateTime.of(2025, 11, 9, 0, 0);

    WinningNumbersDto expectedResult =
            new WinningNumbersDto(
                    Set.of(1, 2, 3, 4, 5, 6),
                    drawDateGeneratorStub.generateNextDrawDate(currentDateTime)
            );

    @Test
    public void should_return_correct_result() {
        // when
        WinningNumbersDto dto = facade.generate();

        // then
        assertThat(dto).isEqualTo(expectedResult);
    }

    @Test
    public void should_throw_exception_when_no_winning_numbers_exist() {
        // given
        LocalDateTime missingDate = LocalDateTime.of(2025, 11, 2, 20, 0);

        // expect
        assertThrows(
                WinningNumbersNotFoundException.class,
                () -> facade.retrieveWinningNumbersDtoByDraw(missingDate)
        );
    }

    @Test
    public void should_throw_exception_when_no_winning_numbers_for_requested_date() {
        // given
        LocalDateTime missingDate = LocalDateTime.of(2025, 11, 2, 20, 0);

        // when
        drawDateGeneratorStub.generateNextDrawDate(LocalDateTime.of(2019, 1, 12, 10, 0));

        // then
        assertThrows(
                WinningNumbersNotFoundException.class,
                () -> facade.retrieveWinningNumbersDtoByDraw(missingDate)
        );
    }

    @Test
    public void should_return_correct_winning_numbers_for_existing_draw_date() {
        // given
        WinningNumbersDto created = facade.generate();

        // when
        WinningNumbersDto retrieved = facade.retrieveWinningNumbersDtoByDraw(created.date());

        // then
        assertThat(retrieved).isEqualTo(expectedResult);
    }
}
