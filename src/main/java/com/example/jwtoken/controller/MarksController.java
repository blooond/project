package com.example.jwtoken.controller;

import com.example.jwtoken.dto.MarkDto;
import com.example.jwtoken.model.Mark;
import com.example.jwtoken.service.MarkService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MarksController {

    private final MarkService markService;

    @PostMapping("/marks/subjects/{subjectId}/new")
    public Mark create(@PathVariable Long subjectId,
                       @RequestBody MarkDto dto) {
        return markService.create(dto, subjectId);
    }
}
