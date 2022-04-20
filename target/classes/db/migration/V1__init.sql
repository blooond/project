create sequence role_sequence start 1 increment 1;
create sequence subject_sequence start 1 increment 1;
create sequence user_sequence start 1 increment 1;

create table users(
    id bigint primary key not null,
    username varchar(255) unique not null,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    created timestamp with time zone not null,
    updated timestamp with time zone not null,
    status varchar(255) not null
);

create table subjects(
    id bigint primary key not null,
    name varchar(255) unique not null,
    teacher_id bigint not null,
    created timestamp with time zone not null,
    updated timestamp with time zone not null,
    status varchar(255) not null,
    foreign key (teacher_id) references users(id) on delete CASCADE
);

create table roles(
    id bigint primary key not null,
    name varchar(255) unique not null,
    created timestamp with time zone not null,
    updated timestamp with time zone not null,
    status varchar(255) not null
);

create table user_roles(
    user_id bigint not null,
    role_id bigint not null,
    primary key(user_id, role_id),
    foreign key (user_id) references users(id) on delete cascade,
    foreign key (role_id) references roles(id)
);

create table students_subjects(
    student_id bigint not null,
    subject_id bigint not null,
    primary key(student_id, subject_id),
    foreign key (student_id) references users(id) on delete cascade,
    foreign key (subject_id) references subjects(id) on delete cascade
);

create table marks(
    student_id bigint not null,
    subject_id bigint not null,
    primary key(student_id, subject_id),
    foreign key (student_id) references users(id) on delete cascade,
    foreign key (subject_id) references subjects(id) on delete cascade,
    rate int not null
);

alter table marks
    add constraint marks_student_constraint
        foreign key (student_id)
            references users(id);

alter table marks
    add constraint marks_subject_constraint
        foreign key (subject_id)
            references subjects(id);

alter table students_subjects
    add constraint ss_student_constraint
        foreign key (student_id)
            references users(id);

alter table students_subjects
    add constraint ss_subject_constraint
        foreign key (subject_id)
            references subjects(id);

alter table subjects
    add constraint subjects_teacher_constraint
        foreign key (teacher_id)
            references users(id);

alter table user_roles
    add constraint ur_role_constraint
        foreign key (role_id)
            references roles(id);

alter table user_roles
    add constraint ur_user_constraint
        foreign key (user_id)
            references users(id);