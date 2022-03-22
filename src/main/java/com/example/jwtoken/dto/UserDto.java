package com.example.jwtoken.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private String username;
    private String name;
    private String email;
    private String password;
    private List<String> roles;
}
