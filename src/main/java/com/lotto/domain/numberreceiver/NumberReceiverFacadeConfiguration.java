package com.lotto.domain.numberreceiver;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerable;
import com.lotto.domain.general.HashGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class NumberReceiverFacadeConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public NumberReceiverFacade numberReceiverFacade(
            TicketRepository ticketRepository,
            Clock clock,
            HashGenerable hashGenerator) {
        return create(ticketRepository, clock, hashGenerator);
    }

    public NumberReceiverFacade create(
            TicketRepository ticketRepository,
            Clock clock,
            HashGenerable hashGenerator) {
        NumberValidator numberValidator = new NumberValidator();
        DrawDateGenerator drawDateGenerator = new DrawDateGenerator();
        return new NumberReceiverFacade(
                numberValidator,
                ticketRepository,
                clock,
                hashGenerator,
                drawDateGenerator
        );
    }

}
