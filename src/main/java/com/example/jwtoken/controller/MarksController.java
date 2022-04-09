package com.example.jwtoken.controller;

import com.example.jwtoken.dto.MarkDto;
import com.example.jwtoken.model.Mark;
import com.example.jwtoken.repository.RoleRepository;
import com.example.jwtoken.service.MarkService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/marks")
public class MarksController {

    private final MarkService markService;

    @GetMapping("/subjects/{subjectId}/new")
    public String add(@PathVariable Long subjectId,
                      Model model) {
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("markDto", new MarkDto());

        return "marks/new";
    }

    @PostMapping("/subjects/{subjectId}/new")
    public String create(@PathVariable Long subjectId,
                         @RequestBody @ModelAttribute MarkDto dto) {
        markService.create(dto, subjectId);

        return "redirect:/subjects/" + subjectId;
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
    public String delete(@PathVariable Long subjectId) {
        markService.delete(subjectId);

        return "redirect:/subjects/" + subjectId;
    }
}
