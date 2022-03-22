package com.example.jwtoken.service;

import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.Status;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.example.jwtoken.model.Role;
import com.example.jwtoken.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jwtoken.repository.RoleRepository;
import com.example.jwtoken.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

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

    public List<User> getAll() {
        List<User> allUsers = userRepository.findAll();
        log.info("{} users found", allUsers.size());
        return allUsers;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            log.info("User with username '{}' doesn't exist", username);
            throw new IllegalStateException();
        }

        log.info("User was found by username '{}'", username);
        return userOptional;
    }

    public Optional<User> findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            log.info("User with id '{}' doesn't exist", id);
            throw new IllegalStateException();
        }

        log.info("User found by id '{}'", id );
        return userOptional;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("User with id  {} was deleted", id);
    }
}
