package com.example.jwtoken.security;

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

        if (userOptional.isEmpty()) {
            log.info("Can't find user with username '{}'", username);
            throw new IllegalStateException();
        }

        JwtUser jwtUser = JwtUserFactory.create(userOptional.get());
        log.info("Loaded user with username '{}'", username);

        return jwtUser;
    }
}
