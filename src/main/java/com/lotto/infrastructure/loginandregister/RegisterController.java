package com.lotto.infrastructure.loginandregister;

import com.lotto.domain.loginandregister.LoginAndRegisterFacade;
import com.lotto.domain.loginandregister.RegisterRequestDto;
import com.lotto.domain.loginandregister.RegistrationResultDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RegisterController {

    private final LoginAndRegisterFacade facade;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResultDto> register(@RequestBody RegisterRequestDto registerRequest) {
        String encodedPassword = encoder.encode(registerRequest.password());
        RegistrationResultDto registerResult = facade.register(
                new RegisterRequestDto(registerRequest.username(), encodedPassword)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResult);
    }

}
