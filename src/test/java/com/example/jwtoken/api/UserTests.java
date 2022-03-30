package com.example.jwtoken.api;

import com.example.jwtoken.controller.UsersController;
import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.Status;
import com.example.jwtoken.model.User;
import com.example.jwtoken.repository.UserRepository;
import com.example.jwtoken.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UserTests {

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    private final User student;
    private final User teacher;
    private final User incorrectUser;

    @Autowired
    public UserTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        List<Role> studentRoles = new ArrayList<>();
        studentRoles.add(new Role(
                new Date(),
                new Date(),
                Status.ACTIVE,
                "student")
        );

        List<Role> teacherRoles = new ArrayList<>();
        teacherRoles.add(new Role(
                new Date(),
                new Date(),
                Status.ACTIVE,
                "teacher")
        );

        student = new User(
                "egorchernooky",
                "egor",
                "egor@gmail.com",
                "password",
                studentRoles,
                new Date(),
                new Date(),
                Status.ACTIVE
        );

        teacher = new User(
                "renedekart",
                "rene",
                "rene@gmail.com",
                "password",
                teacherRoles,
                new Date(),
                new Date(),
                Status.ACTIVE
        );

        incorrectUser = new User(
                null,
                "",
                "",
                "",
                null,
                new Date(),
                new Date(),
                Status.ACTIVE
        );
    }

    @Test
    public void testRegistration_success() throws Exception {
        List<String> studentRoles = new ArrayList<>();
        for (Role role : student.getRoles())
            studentRoles.add(role.getName());

        UserDto studentDto = new UserDto(
                student.getUsername(),
                student.getName(),
                student.getEmail(),
                student.getPassword(),
                studentRoles
        );

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is(student.getUsername())))
                .andExpect(jsonPath("$.name", is(student.getName())))
                .andExpect(jsonPath("$.email", is(student.getEmail())));
    }
}
