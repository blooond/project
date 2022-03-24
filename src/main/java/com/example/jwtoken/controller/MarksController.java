package com.example.jwtoken.controller;

import com.example.jwtoken.dto.MarkDto;
import com.example.jwtoken.model.Mark;
import com.example.jwtoken.service.MarkService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/marks")
public class MarksController {

    private final MarkService markService;

    @PostMapping("/subjects/{subjectId}/new")
    public Mark create(@PathVariable Long subjectId,
                       @RequestBody MarkDto dto) {
        return markService.create(dto, subjectId);
    }

    @GetMapping("/subjects/{subjectId}/all")
    public List<Integer> showAll(@PathVariable Long subjectId) {
        return markService.getAll(subjectId);
    }

    @GetMapping("/subjects/{subjectId}")
    public Integer show(@PathVariable Long subjectId) {
        return markService.show(subjectId);
    }

    @DeleteMapping("/subjects/{subjectId}/delete")
    public void delete(@PathVariable Long subjectId) {
        markService.delete(subjectId);
    }
}
