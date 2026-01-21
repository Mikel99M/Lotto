package com.lotto.infrastructure.resultannouncer;

import com.lotto.domain.resultannouncer.ResultAnnouncerFacade;
import com.lotto.domain.resultannouncer.dto.ResultAnnouncerResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Log4j2
public class ResultAnnouncerController {

    private final ResultAnnouncerFacade facade;

    @GetMapping("/results/{hash}")
    public ResponseEntity<ResultAnnouncerResponseDto> checkResult(@PathVariable String hash) {
        ResultAnnouncerResponseDto result = facade.checkResult(hash);
        return ResponseEntity.ok(result);
    }


}
