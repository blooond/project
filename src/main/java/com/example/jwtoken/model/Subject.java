package com.example.jwtoken.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects")
@NoArgsConstructor
@Getter
@Setter
public class Subject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_generator")
    @SequenceGenerator(
            name = "sequence_generator",
            sequenceName = "subject_sequence",
            allocationSize = 1
    )
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    @JsonIgnore
    private User teacher;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "students_subjects",
            joinColumns = @JoinColumn(name = "subject_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id")
    )
    @JsonIgnore
    private Set<User> enrolledStudents;

    public Subject(String name, User teacher, Date created, Date updated, Status status) {
        super(created, updated, status);
        this.name = name;
        this.teacher = teacher;
        this.enrolledStudents = new HashSet<>();
    }
}
