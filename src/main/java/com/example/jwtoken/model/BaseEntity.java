package com.example.jwtoken.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class BaseEntity {

    @CreatedDate
    private Date created;

    @LastModifiedDate
    private Date updated;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public BaseEntity(Date created, Date updated, Status status) {
        this.created = created;
        this.updated = updated;
        this.status = status;
    }
}
