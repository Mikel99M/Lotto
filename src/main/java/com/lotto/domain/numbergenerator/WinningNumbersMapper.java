package com.lotto.domain.numbergenerator;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WinningNumbersMapper {

    List<WinningNumbersDto> mapWinningNumbersListToWinningNumbersDtoList(final List<WinningNumbers> winningNumbers) {
        return winningNumbers.stream()
                .map(nums -> new WinningNumbersDto(
                        nums.numbers(),
                        nums.date()
                ))
                .toList();
    }

    WinningNumbersDto mapWinningNumbersToWinningNumbersDto(final WinningNumbers winningNumbers) {
        return new WinningNumbersDto(
                winningNumbers.numbers(),
                winningNumbers.date()
        );
    }
}
