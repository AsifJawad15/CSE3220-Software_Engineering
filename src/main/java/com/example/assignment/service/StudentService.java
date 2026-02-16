package com.example.assignment.service;

import com.example.assignment.model.Course;
import com.example.assignment.model.Student;
import com.example.assignment.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * StudentService - Business logic for STUDENT operations
 *
 * IMPORTANT: This service only has READ operations!
 * Students cannot create, update, or delete data.
 *
 * @Service - Tells Spring "this is a service component"
 * @Transactional - Each method runs in a database transaction
 *                  If something fails, changes are rolled back
 */
@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * Constructor Injection
     * Spring automatically provides StudentRepository instance
     */
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Get student by ID
     * Returns null if not found (instead of throwing exception)
     */
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    /**
     * Get student by username
     * Used to load the logged-in student's profile
     */
    public Student getStudentByUsername(String username) {
        return studentRepository.findByUsername(username).orElse(null);
    }

    /**
     * Get courses that a student is enrolled in
     * Returns empty set if student not found
     */
    public Set<Course> getEnrolledCourses(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        return student != null ? student.getEnrolledCourses() : Set.of();
    }
}
