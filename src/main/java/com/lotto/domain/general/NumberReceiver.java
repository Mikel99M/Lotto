package com.lotto.domain.general;

import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface NumberReceiver {

    InputNumberResultDto inputNumbers(Set<Integer> numbersFromUser);

    List<TicketDto> fetchAllTicketDtos(Instant date);

    Instant generateNextDrawDate(Instant now);

    TicketDto fetchTicketByHash(String hash);
}
