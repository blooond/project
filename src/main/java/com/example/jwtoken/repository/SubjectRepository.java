package com.example.jwtoken.repository;

import com.example.jwtoken.model.Subject;
import org.hibernate.boot.jaxb.hbm.spi.SubEntityInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByName(String name);
}
