package com.lotto.infrastructure.numberreceiver;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public record InputNumbersRequestDto(
        @NotNull(message = "{inputNumbers.not.null}")
        @NotEmpty(message = "{inputNumbers.not.empty}")
        @Size(min = 6, max = 6)
        Set<Integer> inputNumbers
) {
}
