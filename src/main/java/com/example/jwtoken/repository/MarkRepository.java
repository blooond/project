package com.example.jwtoken.repository;

import com.example.jwtoken.model.Mark;
import com.example.jwtoken.model.MarkKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkRepository extends JpaRepository<Mark, MarkKey> {

}
