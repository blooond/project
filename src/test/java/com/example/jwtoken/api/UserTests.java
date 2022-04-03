package com.example.jwtoken.api;

import com.example.jwtoken.dto.LoginRequestDto;
import com.example.jwtoken.dto.SubjectDto;
import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.Status;
import com.example.jwtoken.model.User;
import com.example.jwtoken.repository.SubjectRepository;
import com.example.jwtoken.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final SubjectRepository subjectRepository;

    private final User student;
    private final User teacher;
    private final User incorrectUser;

    private static String studentToken = "";
    private static String teacherToken = "";

    @Autowired
    public UserTests(MockMvc mockMvc, ObjectMapper objectMapper, UserService userService,
                     SubjectRepository subjectRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.subjectRepository = subjectRepository;

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
                studentRoles,
                new Date(),
                new Date(),
                null
        );
    }

    @Test
    @Order(1)
    public void testRegistration_success() throws Exception {
        UserDto studentDto = new UserDto(
                student.getUsername(),
                student.getName(),
                student.getEmail(),
                student.getPassword(),
                stringRoles(student.getRoles())
        );

        UserDto teacherDto = new UserDto(
                teacher.getUsername(),
                teacher.getName(),
                teacher.getEmail(),
                teacher.getPassword(),
                stringRoles(teacher.getRoles())
        );

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDto));

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacherDto));

        mockMvc.perform(mockRequest1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is(student.getUsername())))
                .andExpect(jsonPath("$.name", is(student.getName())))
                .andExpect(jsonPath("$.email", is(student.getEmail())));

        mockMvc.perform(mockRequest2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is(teacher.getUsername())))
                .andExpect(jsonPath("$.name", is(teacher.getName())))
                .andExpect(jsonPath("$.email", is(teacher.getEmail())));
    }

    @Test
    @Order(2)
    public void testLogin_success() throws Exception {
        LoginRequestDto studentDto = new LoginRequestDto(
                student.getUsername(),
                student.getPassword()
        );

        LoginRequestDto teacherDto = new LoginRequestDto(
                teacher.getUsername(),
                teacher.getPassword()
        );

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDto));

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacherDto));

        ResultActions studentResult = mockMvc.perform(mockRequest1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is(student.getUsername())));

        ResultActions teacherResult = mockMvc.perform(mockRequest2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is(teacher.getUsername())));

        String str1 = studentResult.andReturn().getResponse().getContentAsString().split("token\":\"")[1];
        studentToken = str1.substring(0, str1.length() - 2);

        String str2 = teacherResult.andReturn().getResponse().getContentAsString().split("token\":\"")[1];
        teacherToken = str2.substring(0, str2.length() - 2);
    }

    @Test
    @Order(3)
    public void testLogin_failure() throws Exception {
        LoginRequestDto dto = new LoginRequestDto(
                incorrectUser.getUsername(),
                incorrectUser.getPassword()
        );

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
        System.out.println(studentToken);
    }

    @Test
    @Order(4)
    public void testGetUser_success() throws Exception {
        Long id = userService.findByUsername(teacher.getUsername()).get().getId();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is(teacher.getUsername())))
                .andExpect(jsonPath("$.name", is(teacher.getName())))
                .andExpect(jsonPath("$.email", is(teacher.getEmail())));
    }

    @Test
    @Order(4)
    public void testGetUser_failure() throws Exception {
        Long id = userService.findByUsername(teacher.getUsername()).get().getId();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(5)
    public void testUpdateUser_success() throws Exception {
        UserDto teacherDto = new UserDto(
                null,
                "rene dekart",
                "rene@mail.ru",
                null,
                null
        );

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken)
                .content(objectMapper.writeValueAsString(teacherDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(teacherDto.getName())))
                .andExpect(jsonPath("$.email", is(teacherDto.getEmail())));
    }

    @Test
    @Order(6)
    public void testUpdateUser_failure() throws Exception {
        UserDto teacherDto = new UserDto(
                null,
                "rene dekart",
                "rene@mail.ru",
                null,
                null
        );

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/users/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacherDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(7)
    public void testEnrollUser_success() throws Exception {
        String subjectName = "math";
        SubjectDto subjectDto = new SubjectDto(
                subjectName
        );

        MockHttpServletRequestBuilder createSubject = MockMvcRequestBuilders.post("/subjects/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken)
                .content(objectMapper.writeValueAsString(subjectDto));

        mockMvc.perform(createSubject)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(subjectName)));

        Long subjectId = subjectRepository.findByName(subjectName).get().getId();
        MockHttpServletRequestBuilder enrollSubject = MockMvcRequestBuilders.put("/students/enroll/" +
                        subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken);

        mockMvc.perform(enrollSubject)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username", is(student.getUsername())))
                .andExpect(jsonPath("$.studentSubjects", notNullValue()));
    }

    @Test
    @Order(8)
    public void testEnrollUser_failure() throws Exception {
        String subjectName = "russian";
        SubjectDto subjectDto = new SubjectDto(
                subjectName
        );

        MockHttpServletRequestBuilder createSubject = MockMvcRequestBuilders.post("/subjects/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken)
                .content(objectMapper.writeValueAsString(subjectDto));

        mockMvc.perform(createSubject)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(subjectName)));

        Long subjectId = subjectRepository.findByName(subjectName).get().getId();
        MockHttpServletRequestBuilder enrollSubject = MockMvcRequestBuilders.put("/students/enroll/" +
                        subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(enrollSubject)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(9)
    public void testDeleteUser_success() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken);

        mockMvc.perform(mockRequest)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Order(10)
    public void testDeleteUser_failure() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().is4xxClientError());
    }

    static List<String> stringRoles(List<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
