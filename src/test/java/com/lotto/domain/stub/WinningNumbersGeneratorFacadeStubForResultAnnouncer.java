package com.lotto.domain.stub;

import com.lotto.domain.general.WinningNumbersGenerator;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WinningNumbersGeneratorFacadeStubForResultAnnouncer implements WinningNumbersGenerator {

    private final Map<Instant, Set<Integer>> winningNumbersByDraw = new HashMap<>();

    public void addWinningNumbers(Instant drawDate, Set<Integer> numbers) {
        winningNumbersByDraw.put(drawDate, numbers);
    }

    @Override
    public WinningNumbersDto generate() {
        return null;
    }

    @Override
    public List<WinningNumbersDto> retrieveAllWinningNumbersDtos() {
        return List.of();
    }

    @Override
    public WinningNumbersDto retrieveWinningNumbersDtoByDraw(Instant drawDate) {
        return new WinningNumbersDto(
                winningNumbersByDraw.getOrDefault(drawDate, Set.of()),
                drawDate
        );
    }
}
