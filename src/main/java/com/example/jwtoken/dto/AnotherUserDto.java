package com.example.jwtoken.dto;

import com.example.jwtoken.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnotherUserDto {

    private String username;
    private String name;
    private String email;
    private String password;
    private Role role;
}
