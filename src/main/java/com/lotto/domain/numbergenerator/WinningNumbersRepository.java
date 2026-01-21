package com.lotto.domain.numbergenerator;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WinningNumbersRepository extends MongoRepository<WinningNumbers, String> {

    WinningNumbers save(WinningNumbers winningNumbers);

    List<WinningNumbers> findAll();

    Optional<WinningNumbers> findWinningNumbersByDate(LocalDateTime drawDate);
}
