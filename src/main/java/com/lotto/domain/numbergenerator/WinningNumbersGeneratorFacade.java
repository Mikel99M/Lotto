package com.lotto.domain.numbergenerator;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerable;
import com.lotto.domain.general.WinningNumbersGenerator;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import lombok.AllArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class WinningNumbersGeneratorFacade implements WinningNumbersGenerator {

    private final WinningNumbersRepository winningNumbersRepository;
    private final RandomNumberGenerable numbersGenerator;
    private final WinningNumbersMapper winningNumbersMapper;
    private final HashGenerable hashGenerator;
    private final DrawDateGenerator drawDateGenerator;
    private final WinningNumbersGeneratorFacadeConfigurationProperties properties;
    private final Clock clock;

    public WinningNumbersDto generate() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime drawDate = drawDateGenerator.generateNextDrawDate(now);
        String hash = hashGenerator.generateHash();
        SixRandomNumbersDto dto = numbersGenerator.generateSixRandomNumbers(properties.lowerBand(), properties.upperBand());
        Set<Integer> winningNums = dto.numbers();

        WinningNumbers winningNumbers = new WinningNumbers.WinningNumbersBuilder()
                .hash(hash)
                .numbers(winningNums)
                .date(drawDate)
                .build();
        WinningNumbers savedNumbers = winningNumbersRepository.save(winningNumbers);
        return new WinningNumbersDto(
                savedNumbers.numbers(),
                savedNumbers.date()
        );
    }

    public List<WinningNumbersDto> retrieveAllWinningNumbersDtos() {
        List<WinningNumbers> winningNumbersList = winningNumbersRepository.findAll();
        return winningNumbersMapper.mapWinningNumbersListToWinningNumbersDtoList(winningNumbersList);
    }

    public WinningNumbersDto retrieveWinningNumbersDtoByDraw(LocalDateTime drawDate) {
        WinningNumbers winningNumbers = winningNumbersRepository.findWinningNumbersByDate(drawDate).orElseThrow(
                () -> new WinningNumbersNotFoundException("No winning numbers found for draw date: " + drawDate)
        );
        return winningNumbersMapper.mapWinningNumbersToWinningNumbersDto(winningNumbers);
    }

}
