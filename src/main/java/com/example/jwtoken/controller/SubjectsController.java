package com.example.jwtoken.controller;

import com.example.jwtoken.dto.SubjectDto;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.service.SubjectService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/subjects")
public class SubjectsController {

    private SubjectService subjectService;

    @PostMapping("/new")
    public Subject create(@RequestBody SubjectDto dto) {
        return subjectService.create(dto);
    }

    @GetMapping("/{subjectId}")
    public Subject show(@PathVariable Long subjectId) {
        return subjectService.show(subjectId);
    }

    @PutMapping("/{subjectId}/update")
    public Subject update(@PathVariable Long subjectId,
                          @RequestBody SubjectDto dto) {
        return subjectService.update(subjectId, dto);
    }
}
