package com.example.jwtoken;

import com.example.jwtoken.model.*;
import com.example.jwtoken.repository.MarkRepository;
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
public class MarkTests {

    private final MarkRepository markRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    private final MarkKey id = new MarkKey(1L,1L);

    @Autowired
    public MarkTests(MarkRepository markRepository,
                     RoleRepository roleRepository,
                     UserRepository userRepository,
                     SubjectRepository subjectRepository,
                     PasswordEncoder passwordEncoder) {
        this.markRepository = markRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    public void testCreateMark() {
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

       Subject subject = subjectRepository.save(new Subject(
                "math",
                teacher,
                new Date(),
                new Date(),
                Status.ACTIVE)
        );

        MarkKey id = new MarkKey(student.getId(), subject.getId());

        Mark mark = markRepository.save(new Mark(
                id,
                student,
                subject,
                9)
        );

        assertThat(markRepository.findById(id).get()).isEqualTo(mark);
    }

    @Test
    @Order(2)
    public void testGetMark() {
        Optional<Mark> markOptional = markRepository.findById(id);
        markOptional.ifPresentOrElse(
                mark -> assertThat(mark.getMarkKey()).isEqualTo(id),
                () -> {
                    log.info("Mark with this id not found");
                    throw new IllegalStateException();
                }
        );
    }

    @Test
    @Order(3)
    public void testUpdateMark() {
        Optional<Mark> markOptional = markRepository.findById(id);
        markOptional.ifPresentOrElse(
                mark -> {
                    final int newRate = 6;
                    mark.setRate(newRate);
                    Mark updatedMark = markRepository.save(mark);
                    assertThat(updatedMark.getRate()).isEqualTo(newRate);
                },
                () -> {
                    log.info("Mark with this id not found");
                    throw new IllegalStateException();
                }
        );
    }

    @Test
    @Order(4)
    public void testDeleteMark() {
        Optional<Mark> markOptional = markRepository.findById(id);
        markOptional.ifPresentOrElse(
                mark -> {
                    markRepository.delete(mark);
                    Mark deletedMark = null;
                    Optional<Mark> markOptional1 = markRepository.findById(id);
                    if (markOptional1.isPresent())
                        deletedMark = markOptional1.get();
                    assertThat(deletedMark).isNull();
                },
                () -> {
                    log.info("Mark with this id not found");
                    throw new IllegalStateException();
                }
        );
    }
}
