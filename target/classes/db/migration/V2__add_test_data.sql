insert into roles(id, name, created, updated, status)
values (1, 'student', now(), now(), 'ACTIVE'),
       (2, 'teacher', now(), now(), 'ACTIVE');

insert into users(id, username, name, email, password, created, updated, status)
values (1, 'egorchernooky', 'Egor', 'egor@gmail.com', '$2a$04$YfRskpevKPawRpKHRhCI9OpeMGVktYNztpohxn2F6dGKflEaO9hdS', now(), now(), 'ACTIVE'),
       (2, 'renedekart', 'Rene', 'rene@gmail.com', '$2a$04$I0k27NvZ2NhilyiirNxFJOLo56Jh6DIVGzO7rZY5CYGaOehFMjTMy', now(), now(), 'ACTIVE');

insert into user_roles(user_id, role_id)
values (1, 1), (2, 2);

insert into subjects(id, name, teacher_id, created, updated, status)
values (1, 'Math', 2, now(), now(), 'ACTIVE');

insert into students_subjects(student_id, subject_id)
values (1, 1);

insert into marks(student_id, subject_id, rate)
values (1, 1, 9);