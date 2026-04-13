package com.lotto.domain.numbergenerator;

import com.lotto.domain.general.DrawDateGenerator;
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
import java.util.Optional;

@AllArgsConstructor
@Service
public class WinningNumbersGeneratorFacade implements WinningNumbersGenerator {

    private final WinningNumbersRepository winningNumbersRepository;
    private final RandomNumberGenerable numbersGenerator;
    private final WinningNumbersMapper winningNumbersMapper;
    private final DrawDateGenerator drawDateGenerator;
    private final WinningNumbersGeneratorFacadeConfigurationProperties properties;
    private final Clock clock;
    private final ZoneId businessZone;

    @CacheEvict(value = "recentWinningNumbers", allEntries = true)
    public WinningNumbersDto generate() {
        ZonedDateTime now = clock.instant().atZone(businessZone);
        Instant drawDate = drawDateGenerator.generateNextDrawDate(now.toInstant());

        Optional<WinningNumbers> existing = winningNumbersRepository.findWinningNumbersByDate(drawDate);
        if (existing.isPresent()) {
            WinningNumbers wn = existing.get();
            return new WinningNumbersDto(wn.numbers(), wn.date());
        }

        SixRandomNumbersDto dto = numbersGenerator.generateSixRandomNumbers(properties.lowerBand(), properties.upperBand());

        WinningNumbers winningNumbers = new WinningNumbers.WinningNumbersBuilder()
                .numbers(dto.numbers())
                .date(drawDate)
                .build();

        WinningNumbers saved = winningNumbersRepository.save(winningNumbers);

        return new WinningNumbersDto(saved.numbers(), saved.date());
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
