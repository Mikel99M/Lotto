package com.lotto.domain.general;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;

import java.time.Instant;
import java.util.List;

public interface WinningNumbersGenerator {

    WinningNumbersDto generate();

    List<WinningNumbersDto> retrieveAllWinningNumbersDtos();

    WinningNumbersDto retrieveWinningNumbersDtoByDraw(Instant drawDate);
}
