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
        if (subjectOptional.isPresent()) {
            log.info("Subject with name '{}' already exists", dto.getName());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Optional<User> teacher = userService.findByUsername(jwtUser.getUsername());

        if (teacher.isEmpty()) {
            log.info("IN Subject create()");
            throw new IllegalStateException("Teacher optional is empty");
        }

        Subject subject = new Subject(
                dto.getName(),
                teacher.get(),
                new Date(),
                new Date(),
                Status.ACTIVE
        );

        subjectRepository.save(subject);

        log.info("Subject with name '{}' created", dto.getName());
        return subject;
    }
}
