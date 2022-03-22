package com.example.jwtoken.repository;

import com.example.jwtoken.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById(Long id);
    Optional<User> findByUsername(String username);
}
