package com.example.jwtoken.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private String username;

    private String name;

    private String email;

    private String password;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToMany(mappedBy = "teacher")
    private List<Subject> teacherSubjects;

    @ManyToMany(mappedBy = "enrolledStudents")
    private Set<Subject> studentSubjects;

    public User(String username, String name, String email, String password, List<Role> roles,
                Date created, Date updated, Status status) {
        super(created, updated, status);
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        teacherSubjects = new ArrayList<>();
        studentSubjects = new HashSet<>();
    }
}
