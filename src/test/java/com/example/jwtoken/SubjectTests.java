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
public class SubjectTests {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    static final Long SUBJECT_ID = 1L;

    @Autowired
    public SubjectTests(UserRepository userRepository,
                        SubjectRepository subjectRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    public void testCreateSubject() {
        List<Role> teacherRoles = new ArrayList<>();
        teacherRoles.add(roleRepository.findByName("teacher"));
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

        Subject subject = subjectRepository.save(new Subject(
                "math",
                teacher,
                new Date(),
                new Date(),
                Status.ACTIVE)
        );

        assertThat(subject.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void testGetSubject() {
        Optional<Subject> subjectOptional = subjectRepository.findById(SUBJECT_ID);
        subjectOptional.ifPresentOrElse(
                subject -> assertThat(subject.getId()).isEqualTo(SUBJECT_ID),
                () -> {
                    log.info("Subject with id '{}' not found", SUBJECT_ID);
                    throw new IllegalStateException();
                }
        );
    }

    @Test
    @Order(3)
    public void testUpdateSubject() {
        Optional<Subject> subjectOptional = subjectRepository.findById(SUBJECT_ID);
        subjectOptional.ifPresentOrElse(
                subject -> {
                    final String newName = "english";
                    subject.setName(newName);
                    Subject updatedSubject = subjectRepository.save(subject);
                    assertThat(updatedSubject.getName()).isEqualTo(newName);
                },
                () -> {
                    log.info("Subject with id '{}' not found", SUBJECT_ID);
                    throw new IllegalStateException();
                }
        );
    }

    @Test
    @Order(4)
    public void testDeleteSubject() {
        Optional<Subject> subjectOptional = subjectRepository.findById(SUBJECT_ID);
        final String SUBJECT_NAME = "math";

        subjectOptional.ifPresentOrElse(
                subject -> {
                    subjectRepository.delete(subject);
                    Subject deletedSubject = null;
                    Optional<Subject> subjectOptional1 = subjectRepository.findByName(SUBJECT_NAME);
                    if (subjectOptional1.isPresent())
                        deletedSubject = subjectOptional1.get();
                    assertThat(deletedSubject).isNull();
                },
                () -> {
                    log.info("Subject with id '{}' not found", SUBJECT_ID);
                    throw new IllegalStateException();
                }
        );
    }
}
