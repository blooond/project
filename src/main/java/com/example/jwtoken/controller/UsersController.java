package com.example.jwtoken.controller;

import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.User;
import com.example.jwtoken.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
public class UsersController {

    private UserService userService;

    @GetMapping("/users/{userId}")
    public User show(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @PostMapping("/registration")
    public User registration(@RequestBody UserDto dto) {
        return userService.registration(dto);
    }

    @PutMapping("/students/enroll/{subjectId}")
    public User enroll(@PathVariable Long subjectId) {
        return userService.enroll(subjectId);
    }
}
