package com.example.jwtoken.controller;

import com.example.jwtoken.dto.SubjectDto;
import com.example.jwtoken.model.Subject;
import com.example.jwtoken.model.User;
import com.example.jwtoken.repository.RoleRepository;
import com.example.jwtoken.security.jwt.JwtUser;
import com.example.jwtoken.service.MarkService;
import com.example.jwtoken.service.SubjectService;
import com.example.jwtoken.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/subjects")
public class SubjectsController {

    private SubjectService subjectService;
    private UserService userService;
    private MarkService markService;
    private RoleRepository roleRepository;

    @GetMapping("/new")
    public String add(Model model) {
        model.addAttribute("subject", new SubjectDto());
        return "subjects/new";
    }

    @PostMapping("/new")
    public String create(@RequestBody @ModelAttribute SubjectDto dto) {
        Long id = subjectService.create(dto).getId();
        return "redirect:/subjects/" + id;
    }

    @GetMapping("/{subjectId}")
    public String show(@PathVariable Long subjectId,
                        Model model) {
        model.addAttribute("subject", subjectService.findById(subjectId).get());
        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("role", roleRepository.findByName("student"));
        model.addAttribute("markService", markService);

        return "subjects/show";
    }

    @GetMapping("/{subjectId}/update")
    public String edit(@PathVariable Long subjectId, Model model) {
        model.addAttribute("dto", new SubjectDto());
        model.addAttribute("id", subjectId);

        return "subjects/edit";
    }

    @PutMapping("/{subjectId}/update")
    public String update(@PathVariable Long subjectId,
                         @RequestBody @ModelAttribute  SubjectDto dto) {
        subjectService.update(subjectId, dto);

        return "redirect:/subjects/" + subjectId;
    }

    @DeleteMapping("/{subjectId}/delete")
    public String delete(@PathVariable Long subjectId) {
        subjectService.delete(subjectId);

        return "redirect:/users/" + getCurrentUser().getId();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Optional<User> userOptional = userService.findByUsername(jwtUser.getUsername());

        return userOptional.orElse(null);
    }
}
