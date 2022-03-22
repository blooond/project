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

@Log4j2
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public User registration(UserDto dto) {
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

    public User findByUsername(String username) {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new IllegalStateException("Can't find user with username '" + username + "'");
        }

        log.info("User was found by username '{}'", username);
        return user;
    }

    public User findById(Long id) {
        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new IllegalStateException("Can't find user with id '" + id + "'");
        }

        log.info("User found by id '{}'", id );
        return user;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("User with id  {} was deleted", id);
    }
}
