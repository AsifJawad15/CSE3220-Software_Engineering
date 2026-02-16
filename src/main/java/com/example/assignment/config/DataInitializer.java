package com.example.assignment.config;

import com.example.assignment.model.Role;
import com.example.assignment.model.Student;
import com.example.assignment.model.Teacher;
import com.example.assignment.repository.StudentRepository;
import com.example.assignment.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * DataInitializer - Creates default test users when app starts
 *
 * This is helpful for testing! Without this, you'd have to manually
 * add users to the database before you can login.
 *
 * DEFAULT ACCOUNTS CREATED:
 * ┌──────────┬──────────┬──────────┐
 * │ Role     │ Username │ Password │
 * ├──────────┼──────────┼──────────┤
 * │ TEACHER  │ teacher  │ password │
 * │ STUDENT  │ student  │ test     │
 * └──────────┴──────────┴──────────┘
 */
@Configuration
public class DataInitializer {

    /**
     * CommandLineRunner - Runs automatically when Spring Boot starts
     *
     * @Bean - Spring manages this and runs it at startup
     *
     * The "args ->" is a lambda function that runs when app starts
     */
    @Bean
    public CommandLineRunner initData(TeacherRepository teacherRepository,
                                      StudentRepository studentRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {

            // ═══════════════════════════════════════════════════════════
            // CREATE DEFAULT TEACHER (if not exists)
            // ═══════════════════════════════════════════════════════════
            if (teacherRepository.findByUsername("teacher").isEmpty()) {
                Teacher teacher = new Teacher();
                teacher.setUsername("teacher");
                // Password is ENCODED before saving - never store plain text!
                teacher.setPassword(passwordEncoder.encode("password"));
                teacher.setRole(Role.TEACHER);
                teacher.setFirstName("John");
                teacher.setLastName("Doe");
                teacher.setDesignation("Professor");
                teacher.setEmail("teacher@example.com");
                teacher.setPhoneNumber("123-456-7890");
                teacherRepository.save(teacher);
                System.out.println("✅ Default teacher created - username: teacher, password: password");
            }

            // ═══════════════════════════════════════════════════════════
            // CREATE DEFAULT STUDENT (if not exists)
            // ═══════════════════════════════════════════════════════════
            if (studentRepository.findByUsername("student").isEmpty()) {
                Student student = new Student();
                student.setUsername("student");
                student.setPassword(passwordEncoder.encode("test"));
                student.setRole(Role.STUDENT);
                student.setFirstName("Jane");
                student.setLastName("Smith");
                student.setStudentId("STU001");
                student.setEmail("student@example.com");
                student.setPhoneNumber("098-765-4321");
                studentRepository.save(student);
                System.out.println("✅ Default student created - username: student, password: test");
            }
        };
    }
}
