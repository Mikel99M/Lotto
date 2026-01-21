package com.lotto.infrastructure.numberreceiver;

import com.lotto.domain.numberreceiver.NumberReceiverFacade;
import com.lotto.domain.numberreceiver.dto.InputNumberResultDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class NumberReceiverController {

    private final NumberReceiverFacade facade;

    @PostMapping("/inputNumbers")
    public ResponseEntity<InputNumberResultDto> inputNumbers(@RequestBody @Valid InputNumbersRequestDto request) {
        InputNumberResultDto result = facade.inputNumbers(request.inputNumbers());
        return ResponseEntity.ok(result);
    }

}
