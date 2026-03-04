package com.lotto.domain.stub;

import com.lotto.domain.general.WinningNumbersGenerator;
import com.lotto.domain.numbergenerator.WinningNumbersNotFoundException;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class WinningNumbersGeneratorFacadeStub implements WinningNumbersGenerator {

    public List<WinningNumbersDto> list = new ArrayList<>();

    public void addWinningNumbers(WinningNumbersDto dto) {
        list.add(dto);
    }

    @Override
    public WinningNumbersDto generate() {
        throw new UnsupportedOperationException("Not needed in ResultChecker tests");
    }

    @Override
    public List<WinningNumbersDto> retrieveAllWinningNumbersDtos() {
        return list;
    }

    @Override
    public WinningNumbersDto retrieveWinningNumbersDtoByDraw(Instant drawDate) {
        return list.stream()
                .filter(dto -> dto.date().equals(drawDate))
                .findFirst()
                .orElseThrow(() ->
                        new WinningNumbersNotFoundException("No winning numbers found for date: " + drawDate));
    }
}

