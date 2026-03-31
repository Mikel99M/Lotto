package com.lotto.domain.loginandregister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LoginAndRegisterFacadeTest {

    private UserRepositoryStub stubRepo;
    private LoginAndRegisterFacade facade;

    @BeforeEach
    void setup() {
        stubRepo = new UserRepositoryStub();
        facade = new LoginAndRegisterFacade(stubRepo);
    }

    @Test
    void should_Register_User_When_User_Does_Not_Exist() {
        // given
        RegisterRequestDto requestDto = new RegisterRequestDto("John", "pass");

        // when
        facade.register(requestDto);

        // then
        User user = stubRepo.findByUserName("John").orElseThrow(() -> new UserNotFoundException("User not found"));
        assertThat(user.getUsername()).isEqualTo("John");
        assertThat(user.getPassword()).isEqualTo("pass");
    }

    @Test
    void should_Not_Register_When_User_Exists_and_throw_UserAlreadyExistsException() {
        // given
        User existing = User.builder()
                .email("john@example.com")
                .userName("Old John")
                .password("old")
                .build();

        stubRepo.save(existing);
        RegisterRequestDto req = new RegisterRequestDto("John", "pass");

        // when
        facade.register(req);

        // then
        User user = stubRepo.findByEmail("john@example.com").orElseThrow();
        assertThat("Old John").isEqualTo(user.getUsername());
        assertThat("old").isEqualTo(user.getPassword());
        assertThatThrownBy(() -> facade.register(req)).isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void should_Delete_User_When_Password_Matches() {
        // given
        User user = User.builder()
                .email("john@example.com")
                .password("pass")
                .userName("John")
                .build();
        stubRepo.save(user);

        DeleteRequestDto req = new DeleteRequestDto( "pass", "john@example.com");

        // when
        facade.deleteUser(req);

        // then
        assertThat(stubRepo.existsByUserName("John")).isFalse();
    }

    @Test
    void should_Not_Delete_When_Password_Does_Not_Match() {
        // given
        User user = User.builder()
                .email("john@example.com")
                .password("pass")
                .userName("John")
                .build();
        stubRepo.save(user);

        DeleteRequestDto req = new DeleteRequestDto( "false password", "john@example.com");

        // when
        facade.deleteUser(req);

        // then
        assertThat(stubRepo.existsByUserName("John")).isTrue();

    }

    @Test
    void should_Throw_When_Deleting_Non_Existing_User() {
        // when & then
        assertThatThrownBy(() -> facade.findByEmail("john@example.com"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void should_Find_User_By_Email() {
        // given
        User user = User.builder()
                .email("john@example.com")
                .userName("John")
                .password("pass")
                .build();
        stubRepo.save(user);

        // when
        UserDto dto = facade.findByEmail("john@example.com");

        // then
        assertThat("John").isEqualTo(dto.name());
        assertThat("john@example.com").isEqualTo(dto.email());
    }

    @Test
    void should_Find_User_By_UserName() {
        // given
        User user = User.builder()
                .email("john@example.com")
                .userName("John")
                .password("pass")
                .build();
        stubRepo.save(user);

        // when
        UserDto dto = facade.findByUserName("John");

        // then
        assertThat(dto.name()).isEqualTo("John");
        assertThat(dto.email()).isEqualTo("john@example.com");
    }

    @Test
    void should_Throw_When_User_Not_Found_By_Email() {
        // when & then
        assertThatThrownBy(() -> facade.findByEmail("john@example.com"))
                .isInstanceOf(BadCredentialsException.class);    }


    @Test
    void should_Throw_When_User_Not_Found_By_UserName() {
        // when & then
        assertThatThrownBy(() -> facade.findByUserName("asdsadasd"))
                .isInstanceOf(BadCredentialsException.class);    }
}