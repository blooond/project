package com.example.jwtoken.service;

import com.example.jwtoken.dto.MarkDto;
import com.example.jwtoken.model.Mark;
import com.example.jwtoken.model.MarkKey;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.model.User;
import com.example.jwtoken.repository.MarkRepository;
import com.example.jwtoken.security.jwt.JwtUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
@Getter
@Setter
public class MarkService {

    private final MarkRepository markRepository;
    private final SubjectService subjectService;
    private final UserService userService;

    public Mark create(MarkDto dto, Long subjectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Optional<User> studentOptional = userService.findByUsername(jwtUser.getUsername());
        studentOptional.ifPresentOrElse(
                student -> log.info("Student with username '{}' loaded", student.getUsername()),
                () -> {
                    log.info("Student with username '{}' doesn't exist", jwtUser.getUsername());
                    throw new IllegalStateException();
                }
        );

        Optional<Subject> subjectOptional = subjectService.findById(subjectId);
        subjectOptional.ifPresentOrElse(
                subject -> log.info("Subject with id '{}' loaded", subject.getId()),
                () -> {
                    log.info("Subject with id '{}' doesn't exist", subjectId);
                    throw new IllegalStateException();
                }
        );

        if (!studentOptional.get().getStudentSubjects().contains(subjectOptional.get())) {
            log.info("Student with username '{}' doesn't attend subject '{}'",
                    studentOptional.get().getUsername(),
                    subjectOptional.get().getName());
        }

        Mark mark = new Mark(
                new MarkKey(jwtUser.getId(), subjectId),
                studentOptional.get(),
                subjectOptional.get(),
                dto.getRate());

        markRepository.save(mark);
        log.info("Mark '{}' saved", mark);

        return mark;
    }

    public List<Integer> getAll(Long subjectId) {
        Optional<Subject> subjectOptional = subjectService.findById(subjectId);
        subjectOptional.ifPresentOrElse(
                subject -> log.info("Subject with id '{}' loaded", subject.getId()),
                () -> {
                    log.info("Subject with id '{}' doesn't exist", subjectId);
                    throw new IllegalStateException();
                }
        );


        return markRepository.findAll().stream()
                .filter(mark -> Objects.equals(mark.getSubject().getId(), subjectId))
                .map(Mark::getRate)
                .collect(Collectors.toList());
    }
}
