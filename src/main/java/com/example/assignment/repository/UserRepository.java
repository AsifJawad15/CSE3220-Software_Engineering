package com.example.assignment.repository;

import com.example.assignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Database operations for User entity
 *
 * EXTENDS JpaRepository<User, Long>:
 * - "User" = The entity type this repository manages
 * - "Long" = The type of the primary key (id)
 *
 * JpaRepository gives you these methods FOR FREE (no code needed):
 * - save(user)        → INSERT or UPDATE a user
 * - findById(id)      → SELECT * FROM app_users WHERE id = ?
 * - findAll()         → SELECT * FROM app_users
 * - deleteById(id)    → DELETE FROM app_users WHERE id = ?
 * - count()           → SELECT COUNT(*) FROM app_users
 *
 * @Repository - Tells Spring "this is a data access component"
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Custom query method - Spring generates the SQL automatically!
     *
     * Method name: findByUsername
     * Spring reads it as: "find" + "By" + "Username"
     * Generated SQL: SELECT * FROM app_users WHERE username = ?
     *
     * Returns Optional<User> - Either contains the user, or is empty if not found
     * This prevents NullPointerException!
     */
    Optional<User> findByUsername(String username);
}
