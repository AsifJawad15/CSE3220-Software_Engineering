package com.example.assignment.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Course Entity - Represents an academic course
 * Examples: "Database Systems", "Web Development", "Data Structures"
 */
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String code;
    private String description;
    private Integer credits;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToMany(mappedBy = "enrolledCourses")
    private Set<Student> enrolledStudents = new HashSet<>();

    // Default constructor
    public Course() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Set<Student> getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(Set<Student> enrolledStudents) { this.enrolledStudents = enrolledStudents; }
}
