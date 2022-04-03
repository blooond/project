package com.example.jwtoken.api;

import com.example.jwtoken.dto.LoginRequestDto;
import com.example.jwtoken.dto.MarkDto;
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
public class MarkTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final SubjectRepository subjectRepository;

    private static Subject math;
    private static final int rate = 9;

    private static String studentToken = "";
    private static String teacherToken = "";

    @Autowired
    public MarkTests(MockMvc mockMvc, ObjectMapper objectMapper,
                        SubjectRepository subjectRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.subjectRepository = subjectRepository;
    }

    @Test
    @Order(1)
    public void testCreateMark_success() throws Exception {
        initialization();

        MarkDto markDto = new MarkDto(
                rate
        );

        MockHttpServletRequestBuilder createMark = MockMvcRequestBuilders.post("/marks/subjects/" +
                math.getId() + "/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken)
                .content(objectMapper.writeValueAsString(markDto));

        mockMvc.perform(createMark)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.rate", is(rate)));
    }

    @Test
    @Order(2)
    public void testCreateMark_failure() throws Exception {
        MarkDto markDto = new MarkDto(
                rate
        );

        MockHttpServletRequestBuilder createMark = MockMvcRequestBuilders.post("/marks/subjects/" +
                        math.getId() + "/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken)
                .content(objectMapper.writeValueAsString(markDto));

        mockMvc.perform(createMark)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(3)
    public void testGetMark_success() throws Exception {
        MockHttpServletRequestBuilder getMark = MockMvcRequestBuilders.get("/marks/subjects/" +
                    math.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken);

        mockMvc.perform(getMark)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(rate)));
    }

    @Test
    @Order(4)
    public void testGetMark_failure() throws Exception {
        MockHttpServletRequestBuilder getMark = MockMvcRequestBuilders.get("/marks/subjects/" +
                        math.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken);

        mockMvc.perform(getMark)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(5)
    public void testGetAllMarks_success() throws Exception {
        MockHttpServletRequestBuilder getAllMarks = MockMvcRequestBuilders.get("/marks/subjects/" +
                        math.getId() + "/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken);

        mockMvc.perform(getAllMarks)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(List.of(rate))));
    }

    @Test
    @Order(6)
    public void testGetAllMarks_failure() throws Exception {
        MockHttpServletRequestBuilder getAllMarks = MockMvcRequestBuilders.get("/marks/subjects/" +
                        math.getId() + "/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(getAllMarks)
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(7)
    public void testDeleteMark_success() throws Exception {
        MockHttpServletRequestBuilder getAllMarks = MockMvcRequestBuilders.delete("/marks/subjects/" +
                        math.getId() + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken);

        mockMvc.perform(getAllMarks)
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void testDeleteMark_failure() throws Exception {
        MockHttpServletRequestBuilder getAllMarks = MockMvcRequestBuilders.delete("/marks/subjects/" +
                        math.getId() + "/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken);

        mockMvc.perform(getAllMarks)
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

        //creating subject
        String subjectName = "math";
        SubjectDto subjectDto = new SubjectDto(
                subjectName
        );

        MockHttpServletRequestBuilder createSubject = MockMvcRequestBuilders.post("/subjects/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + teacherToken)
                .content(objectMapper.writeValueAsString(subjectDto));

        mockMvc.perform(createSubject);

        math = subjectRepository.findByName(subjectName).get();

        //enroll student
        MockHttpServletRequestBuilder enrollSubject = MockMvcRequestBuilders.put("/students/enroll/" +
                        math.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer_" + studentToken);

        mockMvc.perform(enrollSubject);
    }
}
