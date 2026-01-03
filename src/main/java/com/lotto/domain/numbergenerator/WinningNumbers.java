package com.lotto.domain.numbergenerator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
public class WinningNumbers {

    @Id
    private String hash;
    private Set<Integer> numbers;
    private LocalDateTime date;
}
