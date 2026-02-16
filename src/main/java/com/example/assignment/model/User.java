package com.example.assignment.model;

import jakarta.persistence.*;

/**
 * User Entity - BASE CLASS for all users (Students and Teachers)
 *
 * This is the PARENT class. Both Student and Teacher will INHERIT from this.
 *
 * ANNOTATIONS EXPLAINED:
 * @Entity - Tells JPA "this class represents a database table"
 * @Table(name = "app_users") - The table name in database will be "app_users"
 * @Inheritance(strategy = InheritanceType.JOINED) - Creates separate tables for Student/Teacher
 */
@Entity
@Table(name = "app_users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Default constructor
    public User() {}

    // All-args constructor
    public User(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
