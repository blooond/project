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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
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

        Subject subject = new Subject(
                dto.getName(),
                getCurrentUser(),
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
                subject -> log.info("Subject was found by id '{}'", id),
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

    @Transactional
    public Subject update(Long subjectId, SubjectDto subjectDto) {
        Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
        subjectOptional.ifPresentOrElse(
                subject -> log.info("Subject was found by id '{}'", subjectId),
                () -> {
                    log.info("Subject with id '{}' doesn't exist", subjectId);
                    throw new IllegalStateException();
                }
        );

        Subject subjectToUpdate = subjectOptional.get();

        if (Objects.equals(subjectToUpdate.getTeacher(), getCurrentUser())) {
            if (subjectDto.getName() != null)
                subjectToUpdate.setName(subjectDto.getName());

        } else {
            log.info("Teacher with username '{}' can't update this subject",
                     getCurrentUser().getUsername());
            throw new IllegalStateException();
        }

        return subjectToUpdate;
    }

    private  User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Optional<User> userOptional = userService.findByUsername(jwtUser.getUsername());
        userOptional.ifPresentOrElse(
                user -> log.info("User was found by username '{}'", user.getUsername()),
                () -> {
                    log.info("User with username '{}' doesn't exist", jwtUser.getUsername());
                    throw new IllegalStateException();
                }
        );
        return userOptional.get();
    }
}
