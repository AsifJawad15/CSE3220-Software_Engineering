package com.example.assignment.repository;

import com.example.assignment.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CourseRepository - Database operations for Course entity
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // JpaRepository provides all basic CRUD operations
}
