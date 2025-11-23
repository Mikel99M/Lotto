package com.lotto.domain.general;

import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import com.lotto.domain.numberreceiver.dto.TicketDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NumberReceiver {

    InputNumberResultDto inputNumbers(Set<Integer> numbersFromUser);

    List<TicketDto> fetchAllTicketDtos(LocalDateTime date);

    LocalDateTime generateNextDrawDate(LocalDateTime now);

    TicketDto fetchTicketByHash(String hash);
}
