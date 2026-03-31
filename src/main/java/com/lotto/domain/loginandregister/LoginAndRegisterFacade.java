package com.lotto.domain.loginandregister;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoginAndRegisterFacade {

    private final UserRepository userRepository;

    public RegistrationResultDto register(RegisterRequestDto requestDto) {

        if (!userRepository.existsByUserName(requestDto.username())) {
            User user = User.builder()
                    .email(requestDto.username())
                    .password(requestDto.password())
                    .userName(requestDto.username())
                    .build();
            User savedUser = userRepository.save(user);

            return new RegistrationResultDto(savedUser.id(), true, savedUser.userName());
        }

        throw new UserAlreadyExistsException("Username is already in use");
    }

    public void deleteUser(DeleteRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email()).orElseThrow(() -> new UserNotFoundException("User with email \"%s\" not found.".formatted(requestDto.email())));
        if (requestDto.password().equals(user.getPassword())) {
            userRepository.delete(user);
        }
    }

    public UserDto findByUserName(String userName) {
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new BadCredentialsException("User with user username \"%s\" not found.".formatted(userName)));
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("User with email \"%s\" not found.".formatted(email)));
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .build();
    }

}
