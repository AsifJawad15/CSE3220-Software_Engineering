package com.example.assignment.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Teacher Entity - Represents a teacher user
 * EXTENDS User - Inherits id, username, password, role from User class
 * Teachers have FULL CRUD access to manage students, courses, departments
 */
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Teacher extends User {

    private String firstName;
    private String lastName;
    private String designation;
    private String email;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    // Default constructor
    public Teacher() {}

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
