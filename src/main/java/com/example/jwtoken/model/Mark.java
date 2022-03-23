package com.example.jwtoken.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "marks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Mark {

    @EmbeddedId
    private MarkKey markKey;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    private Subject subject;

    private int rate;
}
