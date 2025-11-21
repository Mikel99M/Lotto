package com.lotto.domain.numbergenerator;

import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
class WinningNumbers {

    @Id
    String hash;
    Set<Integer> numbers;
    LocalDateTime date;
}
