package com.example.jwtoken.repository;

import com.example.jwtoken.model.Mark;
import com.example.jwtoken.model.MarkKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkRepository extends JpaRepository<Mark, MarkKey> {

}
