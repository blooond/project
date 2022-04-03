package com.example.jwtoken.api;

import com.example.jwtoken.dto.LoginRequestDto;
import com.example.jwtoken.dto.SubjectDto;
import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.Status;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.repository.SubjectRepository;
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

import static com.example.jwtoken.api.UserTests.stringRoles;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubjectTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final SubjectRepository subjectRepository;

    private static String studentToken = "";
    private static String teacherToken = "";

    @Autowired
    public SubjectTests(MockMvc mockMvc, ObjectMapper objectMapper,
                     SubjectRepository subjectRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.subjectRepository = subjectRepository;
    }

    @Test
    @Order(1)
    public void testCreateSubject_success() throws Exception {
        initialization();
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

        String anotherSubjectName = "Economics";
        SubjectDto anotherSubjectDto = new SubjectDto(
                anotherSubjectName
        );

        MockHttpServletRequestBuilder createAnotherSubject = MockMvcRequestBuilders.post("/subjects/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken)
                .content(objectMapper.writeValueAsString(anotherSubjectDto));

        mockMvc.perform(createAnotherSubject)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(anotherSubjectName)));
    }

    @Test
    @Order(2)
    public void testCreateSubject_failure() throws Exception {
        String subjectName = "russian";
        SubjectDto subjectDto = new SubjectDto(
                subjectName
        );

        MockHttpServletRequestBuilder createSubject = MockMvcRequestBuilders.post("/subjects/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken)
                .content(objectMapper.writeValueAsString(subjectDto));

        mockMvc.perform(createSubject)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(3)
    public void testGetSubject_success() throws Exception {
        Subject subject = subjectRepository.findByName("math").get();
        Long subjectId = subject.getId();
        MockHttpServletRequestBuilder getSubject = MockMvcRequestBuilders.get("/subjects/" + subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken);

        mockMvc.perform(getSubject)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(subject.getName())))
                .andExpect(jsonPath("$.id", is(Math.toIntExact(subjectId))));
    }

    @Test
    @Order(4)
    public void testGetSubject_failure() throws Exception {
        Subject subject = subjectRepository.findByName("math").get();
        Long subjectId = subject.getId();
        MockHttpServletRequestBuilder getSubject = MockMvcRequestBuilders.get("/subjects/" + subjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(getSubject)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(5)
    public void testUpdateSubject_success() throws Exception {
        Subject subject = subjectRepository.findByName("math").get();
        SubjectDto subjectDto = new SubjectDto(
              "Mathematics"
        );

        MockHttpServletRequestBuilder updateSubject = MockMvcRequestBuilders.put("/subjects/" +
                subject.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken)
                .content(objectMapper.writeValueAsString(subjectDto));

        mockMvc.perform(updateSubject)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(subjectDto.getName())));
    }

    @Test
    @Order(6)
    public void testUpdateSubject_failure() throws Exception {
        Subject subject = subjectRepository.findByName("Mathematics").get();
        SubjectDto subjectDto = new SubjectDto(
                "math"
        );

        MockHttpServletRequestBuilder updateSubject = MockMvcRequestBuilders.put("/subjects/" +
                        subject.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken)
                .content(objectMapper.writeValueAsString(subjectDto));

        mockMvc.perform(updateSubject)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(7)
    public void testDeleteSubject_success() throws Exception {
        Long subjectId = subjectRepository.findByName("Mathematics").get().getId();
        MockHttpServletRequestBuilder deleteSubject = MockMvcRequestBuilders.delete("/subjects/" +
                        subjectId + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken);

        mockMvc.perform(deleteSubject)
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void testDeleteSubject_failure() throws Exception {
        Long subjectId = subjectRepository.findByName("Economics").get().getId();
        MockHttpServletRequestBuilder deleteSubject = MockMvcRequestBuilders.delete("/subjects/" +
                        subjectId + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken);

        mockMvc.perform(deleteSubject)
                .andExpect(status().is4xxClientError());
    }

    private void initialization() throws Exception {
        String studentUsername = "egorchernooky";
        String teacherUsername = "renedekart";
        String password = "password";

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

        //create new users
        UserDto studentDto = new UserDto(
                studentUsername,
                "egor",
                "egor@gmail.com",
                password,
                stringRoles(studentRoles)
        );

        UserDto teacherDto = new UserDto(
                teacherUsername,
                "rene",
                "rene@gmail.com",
                password,
                stringRoles(teacherRoles)
        );

        MockHttpServletRequestBuilder mockRequest1 = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDto));

        MockHttpServletRequestBuilder mockRequest2 = MockMvcRequestBuilders.post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacherDto));

        mockMvc.perform(mockRequest1);
        mockMvc.perform(mockRequest2);

        //login and getting tokens
        LoginRequestDto studentLoginDto = new LoginRequestDto(
                studentUsername,
                password
        );

        LoginRequestDto teacherLoginDto = new LoginRequestDto(
                teacherUsername,
                password
        );

        MockHttpServletRequestBuilder loginRequest1 = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentLoginDto));

        MockHttpServletRequestBuilder loginRequest2 = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacherLoginDto));

        ResultActions studentResult = mockMvc.perform(loginRequest1);
        ResultActions teacherResult = mockMvc.perform(loginRequest2);

        String str1 = studentResult.andReturn().getResponse().getContentAsString().split("token\":\"")[1];
        studentToken = str1.substring(0, str1.length() - 2);

        String str2 = teacherResult.andReturn().getResponse().getContentAsString().split("token\":\"")[1];
        teacherToken = str2.substring(0, str2.length() - 2);
    }
}
