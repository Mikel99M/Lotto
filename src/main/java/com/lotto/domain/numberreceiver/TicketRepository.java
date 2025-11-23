package com.lotto.domain.numberreceiver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lotto.domain.numberreceiver.dto.TicketDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    List<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate);

    Optional<Ticket> findByHash(String hash);

}
