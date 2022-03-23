package com.example.jwtoken.service;

import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.Status;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.repository.SubjectRepository;
import com.example.jwtoken.security.jwt.JwtUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jwtoken.repository.RoleRepository;
import com.example.jwtoken.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class UserService {

    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SubjectRepository subjectRepository;

    public User registration(UserDto dto) {
        Optional<User> userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isPresent()) {
            log.info("User with username '{}' already exists", dto.getUsername());
            throw new IllegalStateException();
        }

        List<Role> roles = new ArrayList<>();

        for (String name : dto.getRoles())
            roles.add(roleRepository.findByName(name));

        User user = new User(
                dto.getUsername(),
                dto.getName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                roles,
                new Date(),
                new Date(),
                Status.ACTIVE
        );

        userRepository.save(user);

        log.info("User {} successfully registered", user.getUsername());
        return user;
    }

    @Transactional
    public User enroll(Long subjectId) {
        User student = getCurrentUser();

        Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
        subjectOptional.ifPresentOrElse(
                subject -> {
                    student.getStudentSubjects().add(subject);
                    subject.getEnrolledStudents().add(student);
                },
                () -> {
                    log.info("Subject with id '{}' doesn't exist", subjectId);
                    throw new IllegalStateException();
                }
        );

        return student;
    }

    public List<User> getAll() {
        List<User> allUsers = userRepository.findAll();
        log.info("{} users found", allUsers.size());
        return allUsers;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        userOptional.ifPresentOrElse(
                user -> log.info("User was found by username '{}'", username),
                () -> {
                    log.info("User with username '{}' doesn't exist", username);
                    throw new IllegalStateException();
                }
        );

        return userOptional;
    }

    public User findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        userOptional.ifPresentOrElse(
                user -> log.info("User was found by id '{}'", id),
                () -> {
                    log.info("User with id '{}' doesn't exist", id);
                    throw new IllegalStateException();
                }
        );

        return userOptional.get();
    }

    @Transactional
    public User update(UserDto dto) {
        User userToUpdate = getCurrentUser();

        if (dto.getName() != null)
            userToUpdate.setName(dto.getName());

        if (dto.getUsername() != null)
            userToUpdate.setUsername(dto.getUsername());

        if (dto.getEmail() != null)
            userToUpdate.setEmail(dto.getEmail());

        if (dto.getPassword() != null)
            userToUpdate.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userToUpdate;
    }

    public void delete() {
        User userToDelete = getCurrentUser();
        userRepository.deleteById(userToDelete.getId());
        log.info("User with id  {} was deleted", userToDelete.getId());
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Optional<User> userOptional = userRepository.findByUsername(jwtUser.getUsername());
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
