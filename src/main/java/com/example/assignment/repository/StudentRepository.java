package com.example.assignment.repository;

import com.example.assignment.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * StudentRepository - Database operations for Student entity
 *
 * Even though Student extends User, we need a separate repository
 * because Student has additional fields and relationships
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find a student by their username
     * Used during login to verify student credentials
     */
    Optional<Student> findByUsername(String username);

    /**
     * Custom JPQL Query - Java Persistence Query Language
     *
     * @Query - When method naming isn't enough, write your own query
     *
     * "SELECT s FROM Student s" - Select student
     * "LEFT JOIN FETCH s.enrolledCourses" - Also load their courses in one query
     * "WHERE s.id = :id" - Filter by id
     *
     * @Param("id") - Maps the method parameter to :id in the query
     *
     * Why LEFT JOIN FETCH?
     * - Without it, courses load LAZILY (separate query when accessed)
     * - With it, courses load EAGERLY (same query, better performance)
     */
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.enrolledCourses WHERE s.id = :id")
    Optional<Student> findByIdWithCourses(@Param("id") Long id);
}
