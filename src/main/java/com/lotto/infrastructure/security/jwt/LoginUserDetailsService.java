package com.lotto.infrastructure.security.jwt;

import com.lotto.domain.loginandregister.LoginAndRegisterFacade;
import com.lotto.domain.loginandregister.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

@AllArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

    private final LoginAndRegisterFacade loginAndRegisterFacade;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        UserDto user = loginAndRegisterFacade.findByUserName(username);
        return getUser(user);
    }

    private User getUser(UserDto user) {
        return new User(
                user.name(),
                user.password(),
                Collections.emptyList()
        );
    }
}
