package com.example.jwtoken.controller;

import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.User;
import com.example.jwtoken.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/users")
@AllArgsConstructor
public class UsersController {

    private UserService userService;

    @PostMapping("/registration")
    public User registration(@RequestBody UserDto dto) {
        return userService.registration(dto);
    }
}
