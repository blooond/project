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

    @PostMapping("/registration")
    public User registration(@RequestBody UserDto dto) {
        return userService.registration(dto);
    }

    @GetMapping("/users/{userId}")
    public User show(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @PutMapping("/users/update")
    public User update(@RequestBody UserDto dto) {
        return userService.update(dto);
    }

    @DeleteMapping("/users/delete")
    public void delete() {
        userService.delete();
    }

    @PutMapping("/students/enroll/{subjectId}")
    public User enroll(@PathVariable Long subjectId) {
        return userService.enroll(subjectId);
    }
}
