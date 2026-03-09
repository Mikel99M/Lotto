package com.lotto.domain.numberreceiver;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Builder
@Document
record Ticket(
        @Id String id,
        @Indexed(unique = true) String hash,
        Instant purchaseDate,
        Instant drawDate,
        Set<Integer> numbersFromUser) {

}
