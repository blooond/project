package com.example.jwtoken;

import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.Status;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.model.User;
import com.example.jwtoken.repository.RoleRepository;
import com.example.jwtoken.repository.SubjectRepository;
import com.example.jwtoken.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class UserTests {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    static final Long STUDENT_ID = 1L;
    static final Long TEACHER_ID = 2L;

    @Autowired
    public UserTests(UserRepository userRepository,
                     RoleRepository roleRepository,
                     SubjectRepository subjectRepository,
                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.subjectRepository = subjectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @Rollback(value = false)
    @Order(1)
    public void testCreateNewUser() {
        List<Role> studentRoles = new ArrayList<>();
        studentRoles.add(roleRepository.save(
                        new Role(
                                new Date(),
                                new Date(),
                                Status.ACTIVE,
                                "student")
                )
        );
        User student = userRepository.save(new User(
                "studentuser",
                "student",
                "student@gmail.com",
                passwordEncoder.encode("password"),
                studentRoles,
                new Date(),
                new Date(),
                Status.ACTIVE)
        );

        List<Role> teacherRoles = new ArrayList<>();
        teacherRoles.add(roleRepository.save(
                        new Role(
                                new Date(),
                                new Date(),
                                Status.ACTIVE,
                                "teacher")
                )
        );
        User teacher = userRepository.save(new User(
                "teacheruser",
                "teacher",
                "teacher@gmail.com",
                passwordEncoder.encode("password"),
                teacherRoles,
                new Date(),
                new Date(),
                Status.ACTIVE)
        );

        assertThat(student.getId()).isGreaterThan(0);
        assertThat(teacher.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void testGetUser() {
        Optional<User> studentOptional = userRepository.findById(STUDENT_ID);

        studentOptional.ifPresentOrElse(
                student -> assertThat(student.getId()).isEqualTo(STUDENT_ID),
                () -> {
                    log.info("Student with id '{}' not found", STUDENT_ID);
                    throw new IllegalStateException();
                }
        );
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        Optional<User> teacherOptional = userRepository.findById(TEACHER_ID);

        teacherOptional.ifPresentOrElse(
                teacher -> {
                    String newName = "New name";
                    teacher.setName(newName);
                    User updatedUser = userRepository.save(teacher);
                    assertThat(updatedUser.getName()).isEqualTo(newName);
                },
                () -> {
                    log.info("teacher with id '{}' not found", TEACHER_ID);
                    throw new IllegalStateException();
                }
        );
    }

    @Test
    @Order(4)
    public void testDeleteUser() {
        Optional<User> studentOptional = userRepository.findById(STUDENT_ID);
        final String STUDENT_USERNAME = "student";

        studentOptional.ifPresentOrElse(
                student -> {
                    userRepository.delete(student);
                    User deletedStudent = null;
                    Optional<User> studentOptional1 = userRepository.findByUsername(STUDENT_USERNAME);
                    if (studentOptional1.isPresent())
                        deletedStudent = studentOptional1.get();
                    assertThat(deletedStudent).isNull();
                },
                () -> {
                    log.info("Student with id '{}' not found", STUDENT_ID);
                    throw new IllegalStateException();
                }
        );
    }

    @Test
    @Order(5)
    public void testUserEnroll() {
        Subject subject = subjectRepository.save(new Subject(
                "math",
                userRepository.findById(TEACHER_ID).get(),
                new Date(),
                new Date(),
                Status.ACTIVE)
        );

        Optional<User> studentOptional = userRepository.findById(STUDENT_ID);
        studentOptional.ifPresentOrElse(
                student -> {
                    student.getStudentSubjects().add(subject);
                    subject.getEnrolledStudents().add(student);

                    assertThat(student.getStudentSubjects()).contains(subject);
                },
                () -> {
                    log.info("Student with id '{}' not found", STUDENT_ID);
                    throw new IllegalStateException();
                }
        );
    }
}
