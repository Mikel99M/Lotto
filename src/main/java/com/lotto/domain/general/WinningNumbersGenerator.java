package com.lotto.domain.general;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;

import java.time.LocalDateTime;
import java.util.List;

public interface WinningNumbersGenerator {

    WinningNumbersDto generate();

    List<WinningNumbersDto> retrieveAllWinningNumbersDtos();

    WinningNumbersDto retrieveWinningNumbersDtoByDraw(LocalDateTime drawDate);
}
