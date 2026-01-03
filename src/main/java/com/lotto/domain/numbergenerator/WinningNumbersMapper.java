package com.lotto.domain.numbergenerator;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;

import java.util.List;

class WinningNumbersMapper {

    List<WinningNumbersDto> mapWinningNumbersListToWinningNumbersDtoList(final List<WinningNumbers> winningNumbers) {
        return winningNumbers.stream()
                .map(nums -> new WinningNumbersDto(
                        nums.getNumbers(),
                        nums.getDate()
                ))
                .toList();
    }


    WinningNumbersDto mapWinningNumbersToWinningNumbersDto(final WinningNumbers winningNumbers) {
        return new WinningNumbersDto(
                winningNumbers.getNumbers(),
                winningNumbers.getDate()
        );
    }
}
