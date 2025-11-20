package com.lotto.domain.numberreceiver;

import com.lotto.domain.general.DrawDateGenerator;
import com.lotto.domain.general.HashGenerable;
import com.lotto.domain.general.HashGenerator;
import com.lotto.domain.stub.HashGeneratorStub;

import java.time.Clock;

public class NumberReceiverConfiguration {

    public NumberReceiverFacade generateForTests(
            Clock clock,
            TicketRepository ticketRepository,
            HashGenerable hashGenerator
    ) {
        return new NumberReceiverFacade(
                new NumberValidator(),
                ticketRepository,
                clock,
                hashGenerator,
                new DrawDateGenerator()
        );
    }

}