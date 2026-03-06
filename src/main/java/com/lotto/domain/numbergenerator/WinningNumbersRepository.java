package com.lotto.domain.numbergenerator;

import com.lotto.domain.numbergenerator.dto.WinningNumbersDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface WinningNumbersRepository extends MongoRepository<WinningNumbers, String> {

    WinningNumbers save(WinningNumbers winningNumbers);

    List<WinningNumbers> findAll();

    Optional<WinningNumbers> findWinningNumbersByDate(Instant drawDate);

    Optional<WinningNumbersDto> findByDate(Instant drawDateInstant);

}
