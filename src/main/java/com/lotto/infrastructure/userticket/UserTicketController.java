package com.lotto.infrastructure.userticket;

import com.lotto.domain.numberreceiver.NumberReceiverFacade;
import com.lotto.domain.numberreceiver.dto.TicketDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserTicketController {

    private final NumberReceiverFacade numberReceiverFacade;

    @GetMapping("tickets")
    public ResponseEntity<List<TicketDto>> getTickets() {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TicketDto> body = numberReceiverFacade.retrieveAllTicketsByUsername(currentUserName);
        return ResponseEntity.ok().body(body);
    }
}
