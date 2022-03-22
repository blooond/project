package com.example.jwtoken.security;

import io.jsonwebtoken.Jwt;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.example.jwtoken.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.jwtoken.security.jwt.JwtUser;
import com.example.jwtoken.security.jwt.JwtUserFactory;
import com.example.jwtoken.service.UserService;

import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userService.findByUsername(username);

        userOptional.ifPresentOrElse(
                user -> log.info("Loaded user with username '{}'", username),
                () -> {
                    log.info("Can't find user with username '{}'", username);
                    throw new IllegalStateException();
                }
        );

        return JwtUserFactory.create(userOptional.get());
    }
}
