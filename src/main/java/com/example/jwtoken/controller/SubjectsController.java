package com.example.jwtoken.controller;

import com.example.jwtoken.dto.SubjectDto;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.service.SubjectService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/subjects")
public class SubjectsController {

    private SubjectService subjectService;

    @PostMapping("/new")
    private Subject create(@RequestBody SubjectDto dto) {
        return subjectService.create(dto);
    }
}
