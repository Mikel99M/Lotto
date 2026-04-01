package com.lotto.domain.numbergenerator;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerable;
import com.lotto.domain.general.WinningNumbersGenerator;
import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class WinningNumbersGeneratorFacade implements WinningNumbersGenerator {

    private final WinningNumbersRepository winningNumbersRepository;
    private final RandomNumberGenerable numbersGenerator;
    private final WinningNumbersMapper winningNumbersMapper;
    private final HashGenerable hashGenerator;
    private final DrawDateGenerator drawDateGenerator;
    private final WinningNumbersGeneratorFacadeConfigurationProperties properties;
    private final Clock clock;
    private final ZoneId businessZone;

    @CacheEvict(value = "recentWinningNumbers", allEntries = true)
    public WinningNumbersDto generate() {
        ZonedDateTime now = clock.instant().atZone(businessZone);
        Instant drawDate = drawDateGenerator.generateNextDrawDate(now.toInstant());
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

    public WinningNumbersDto retrieveWinningNumbersDtoByDraw(Instant drawDate) {
        WinningNumbers winningNumbers = winningNumbersRepository.findWinningNumbersByDate(drawDate).orElseThrow(
                () -> new WinningNumbersNotFoundException("No winning numbers found for draw date: " + drawDate)
        );
        return winningNumbersMapper.mapWinningNumbersToWinningNumbersDto(winningNumbers);
    }

    @Cacheable(value = "recentWinningNumbers")
    public WinningNumbersDto retrieveMostRecentWinningNumbersDto() {
        ZonedDateTime now = clock.instant().atZone(businessZone);
        ZonedDateTime previousWeekDate = now.minusWeeks(1);
        Instant drawDate = drawDateGenerator.generateNextDrawDate(previousWeekDate.toInstant());

        return retrieveWinningNumbersDtoByDraw(drawDate);
    }

}
