package com.example.assignment.repository;

import com.example.assignment.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DepartmentRepository - Database operations for Department entity
 *
 * We don't need any custom methods here.
 * JpaRepository provides all we need: findAll(), findById(), save(), delete()
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // No custom methods needed - JpaRepository provides everything!
}
