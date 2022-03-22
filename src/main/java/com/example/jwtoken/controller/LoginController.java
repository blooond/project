package com.example.jwtoken.controller;

import com.example.jwtoken.dto.LoginRequestDto;
import com.example.jwtoken.dto.LoginResponseDto;
import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.security.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import com.example.jwtoken.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.jwtoken.security.jwt.JwtProvider;
import com.example.jwtoken.service.UserService;

@RestController
@AllArgsConstructor
public class LoginController {

    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            String password = requestDto.getPassword();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (userDetails == null)
                throw new UsernameNotFoundException("User with username '" + username + "' not found");

            String token = jwtProvider.getToken(username);
            return new ResponseEntity<>(new LoginResponseDto(username, token), HttpStatus.OK);
        } catch (AuthenticationException e) {
             throw new BadCredentialsException("Invalid username or password");
        }
    }
}
