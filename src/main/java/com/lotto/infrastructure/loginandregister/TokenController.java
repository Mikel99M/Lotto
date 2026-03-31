package com.lotto.infrastructure.loginandregister;

import com.lotto.infrastructure.loginandregister.dto.JwtResponseDto;
import com.lotto.infrastructure.loginandregister.dto.TokenRequestDto;
import com.lotto.infrastructure.security.jwt.JwtAuthenticatorFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class TokenController {

    private final JwtAuthenticatorFacade facade;

    @PostMapping("/token")
    public ResponseEntity<JwtResponseDto> authenticateAndGenerateToken(@Valid @RequestBody TokenRequestDto tokenRequest) {
        final JwtResponseDto jwtResponse = facade.authenticateAndGenerateToken(tokenRequest);
                return ResponseEntity.ok(jwtResponse);
    }

}
