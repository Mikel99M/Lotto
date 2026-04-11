package com.lotto.domain.numberreceiver;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

interface TicketRepository extends MongoRepository<Ticket, String> {

    List<Ticket> findAllTicketsByDrawDate(Instant drawDate);

    Optional<Ticket> findByHash(String hash);

    List<Ticket> findAllByOwnerUserName(String ownerUserName);
}
