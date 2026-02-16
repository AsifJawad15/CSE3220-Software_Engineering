package com.example.assignment.repository;

import com.example.assignment.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TeacherRepository - Database operations for Teacher entity
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * Find a teacher by their username
     * Used during login and when loading teacher's profile
     */
    Optional<Teacher> findByUsername(String username);
}
