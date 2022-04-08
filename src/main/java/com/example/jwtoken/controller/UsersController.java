package com.example.jwtoken.controller;

import com.example.jwtoken.dto.AnotherUserDto;
import com.example.jwtoken.dto.AnotherSubjectDto;
import com.example.jwtoken.dto.UserDto;
import com.example.jwtoken.model.User;
import com.example.jwtoken.repository.RoleRepository;
import com.example.jwtoken.repository.SubjectRepository;
import com.example.jwtoken.security.jwt.JwtProvider;
import com.example.jwtoken.security.jwt.JwtUser;
import com.example.jwtoken.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@AllArgsConstructor
public class UsersController {

    private UserService userService;
    private SubjectRepository subjectRepository;
    private JwtProvider jwtProvider;
    private RoleRepository roleRepository;

    @GetMapping("/homepage")
    public String home(Model model) {
        model.addAttribute("user", getCurrentUser());
        return "homepage";
    }

    @GetMapping("/registration")
    public String addUser(Model model) {
        model.addAttribute("dto", new AnotherUserDto());
        model.addAttribute("roles", roleRepository.findAll());
        return "users/registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestBody @ModelAttribute AnotherUserDto dto, UserDto userDto) {
        List<String> roles = new ArrayList<>();
        roles.add(dto.getRole().getName());

        userDto.setUsername(dto.getUsername());
        userDto.setName(dto.getName());
        userDto.setEmail(dto.getEmail());
        userDto.setPassword(dto.getPassword());
        userDto.setRoles(roles);

        userService.registration(userDto);

        return "redirect:/login";
    }

    @GetMapping("/users/{userId}")
    public String show(@PathVariable Long userId, Model model) {
        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("user", userService.findById(userId));
        return "users/show";
    }

    @GetMapping("users/update")
    public String edit(Model model) {
        model.addAttribute("user", getCurrentUser());
        return "users/edit";
    }

    @PutMapping("/users/update")
    public String update(@RequestBody @ModelAttribute UserDto dto,
                         HttpServletResponse res) {

        String token = jwtProvider.getToken(userService.update(dto).getUsername());

        Cookie cookie = new Cookie("Token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setSecure(false);

        res.addCookie(cookie);

        return "redirect:/users/" + getCurrentUser().getId();
    }

    @DeleteMapping("/users/delete")
    public String delete(HttpServletResponse res) {
        Cookie cookie = new Cookie("Token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setSecure(false);

        res.addCookie(cookie);

        userService.delete();

        return "redirect:/";
    }

    @GetMapping("/students/enroll")
    public String enroll(Model model) {
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("dto", new AnotherSubjectDto());

        return "users/enroll";
    }

    @PutMapping("/students/enroll")
    public String enroll(@ModelAttribute AnotherSubjectDto dto) {
        userService.enroll(dto.getSubject().getId());

        return "redirect:/users/" + getCurrentUser().getId();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Optional<User> userOptional = userService.findByUsername(jwtUser.getUsername());

        return userOptional.orElse(null);
    }
}
