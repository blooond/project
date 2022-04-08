package com.example.jwtoken.controller;

import com.example.jwtoken.dto.LoginRequestDto;
import com.example.jwtoken.dto.LoginResponseDto;
import com.example.jwtoken.model.User;
import com.example.jwtoken.security.UserDetailsServiceImpl;
import com.example.jwtoken.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.jwtoken.security.jwt.JwtProvider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class LoginController {

    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private UserDetailsServiceImpl userDetailsService;
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("dto", new LoginRequestDto());
        return "security/login";
    }


    @PostMapping("/login")
    public String login(@RequestBody @ModelAttribute LoginRequestDto requestDto,
                                                  HttpServletResponse response) {
        try {
            String username = requestDto.getUsername();
            String password = requestDto.getPassword();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (userDetails == null)
                throw new UsernameNotFoundException("User with username '" + username + "' not found");

            String token = jwtProvider.getToken(username);

            //cookie creation
            Cookie cookie = new Cookie("Token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setSecure(false);

            response.addCookie(cookie);

            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isPresent())
                return "redirect:/users/" + userOptional.get().getId();
        } catch (AuthenticationException e) {
             throw new BadCredentialsException("Invalid username or password");
        }
        return null;
    }
}
