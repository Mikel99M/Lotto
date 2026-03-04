package com.lotto.domain.numbergenerator;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Builder
@Document(collection = "winning numbers")
public record WinningNumbers(
        @Id String id,
        @Indexed(unique = true) String hash,
        Set<Integer> numbers,
        Instant date) {

}
