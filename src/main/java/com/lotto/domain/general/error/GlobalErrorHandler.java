package com.lotto.domain.general.error;

import com.lotto.domain.numbergenerator.WinningNumbersNotFoundException;
import com.lotto.domain.numberreceiver.TicketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponseMessage> ticketNotFound(TicketNotFoundException e) {
        ErrorResponseMessage response = new ErrorResponseMessage(e.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(WinningNumbersNotFoundException.class)
    public ResponseEntity<ErrorResponseMessage> winningNumbersNotFound(WinningNumbersNotFoundException e) {
        ErrorResponseMessage response = new ErrorResponseMessage(e.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


}
