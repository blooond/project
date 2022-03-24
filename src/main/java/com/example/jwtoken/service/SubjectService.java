package com.example.jwtoken.service;

import com.example.jwtoken.dto.SubjectDto;
import com.example.jwtoken.model.Status;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.model.User;
import com.example.jwtoken.repository.SubjectRepository;
import com.example.jwtoken.security.jwt.JwtUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class SubjectService {

    private SubjectRepository subjectRepository;
    private UserService userService;

    public Subject create(SubjectDto dto) {
        Optional<Subject> subjectOptional = subjectRepository.findByName(dto.getName());
        subjectOptional.ifPresent(
                subject -> log.info("Subject with name '{}' already exists", dto.getName())
        );

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Optional<User> teacherOptional = userService.findByUsername(jwtUser.getUsername());
        teacherOptional.ifPresentOrElse(
                teacher -> log.info("Teacher with username '{}' loaded", teacher.getUsername()),
                () -> {
                    log.info("IN Subject create()");
                    throw new IllegalStateException("Teacher optional is empty");
                }
        );

        Subject subject = new Subject(
                dto.getName(),
                teacherOptional.get(),
                new Date(),
                new Date(),
                Status.ACTIVE
        );

        subjectRepository.save(subject);

        log.info("Subject with name '{}' created", dto.getName());
        return subject;
    }

    public Optional<Subject> findById(Long id) {
        Optional<Subject> subjectOptional = subjectRepository.findById(id);

        subjectOptional.ifPresentOrElse(
                user -> log.info("Subject was found by id '{}'", id),
                () -> {
                    log.info("Subject with id '{}' doesn't exist", id);
                    throw new IllegalStateException();
                }
        );

        return subjectOptional;
    }

    public Subject show(Long subjectId) {
        return findById(subjectId).get();
    }
//
//    public Subject update(SubjectDto subjectDto) {
//
//    }
}
