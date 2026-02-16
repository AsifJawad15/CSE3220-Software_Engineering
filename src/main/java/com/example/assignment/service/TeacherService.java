package com.example.assignment.service;

import com.example.assignment.model.Course;
import com.example.assignment.model.Department;
import com.example.assignment.model.Student;
import com.example.assignment.model.Teacher;
import com.example.assignment.repository.CourseRepository;
import com.example.assignment.repository.DepartmentRepository;
import com.example.assignment.repository.StudentRepository;
import com.example.assignment.repository.TeacherRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TeacherService - Business logic for TEACHER operations
 *
 * IMPORTANT: This service has FULL CRUD operations!
 * Teachers can Create, Read, Update, Delete all data.
 *
 * CRUD = Create, Read, Update, Delete
 *
 * This is the main difference from StudentService which only has READ.
 */
@Service
@Transactional
public class TeacherService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor Injection - Spring provides all these dependencies
     *
     * Why so many repositories? Because Teacher can manage:
     * - Students (CRUD)
     * - Departments (CRUD)
     * - Courses (CRUD)
     * - Their own profile (Update)
     */
    public TeacherService(StudentRepository studentRepository,
                          TeacherRepository teacherRepository,
                          DepartmentRepository departmentRepository,
                          CourseRepository courseRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ═══════════════════════════════════════════════════════════════════
    // STUDENT CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * READ - Get all students
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * READ - Get single student by ID
     */
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    /**
     * READ - Get student with their enrolled courses loaded
     */
    public Student getStudentByIdWithCourses(Long id) {
        return studentRepository.findByIdWithCourses(id).orElse(null);
    }

    /**
     * CREATE or UPDATE - Save a student
     *
     * If student.getId() is null → CREATE new student
     * If student.getId() exists → UPDATE existing student
     *
     * Password handling:
     * - New student: encode the password
     * - Existing student with empty password: keep old password
     * - Existing student with new password: encode new password
     */
    public Student saveStudent(Student student) {
        if (student.getId() == null) {
            // CREATE - New student, encode password
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        } else {
            // UPDATE - Check if password changed
            Student existing = studentRepository.findById(student.getId()).orElse(null);
            if (existing != null && (student.getPassword() == null || student.getPassword().isEmpty())) {
                // Keep old password if new one is empty
                student.setPassword(existing.getPassword());
            } else if (student.getPassword() != null && !student.getPassword().isEmpty()) {
                // Encode new password
                student.setPassword(passwordEncoder.encode(student.getPassword()));
            }
        }
        return studentRepository.save(student);
    }

    /**
     * DELETE - Remove a student by ID
     */
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEACHER OPERATIONS (Profile Management)
    // ═══════════════════════════════════════════════════════════════════

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }

    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username).orElse(null);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    /**
     * UPDATE - Update teacher's own profile
     */
    public Teacher updateTeacher(Teacher teacher) {
        Teacher existing = teacherRepository.findById(teacher.getId()).orElse(null);
        if (existing != null) {
            // Handle password same as student
            if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) {
                teacher.setPassword(existing.getPassword());
            } else {
                teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
            }
            // Keep the role (prevent role tampering)
            teacher.setRole(existing.getRole());
        }
        return teacherRepository.save(teacher);
    }

    // ═══════════════════════════════════════════════════════════════════
    // DEPARTMENT CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════════

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════════
    // COURSE CRUD OPERATIONS
    // ═══════════════════════════════════════════════════════════════════

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════════
    // COURSE ENROLLMENT MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Enroll a student in a course
     * Adds the course to student's enrolledCourses set
     */
    public void enrollStudentInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (student != null && course != null) {
            student.getEnrolledCourses().add(course);
            studentRepository.save(student);
        }
    }

    /**
     * Unenroll a student from a course
     * Removes the course from student's enrolledCourses set
     */
    public void unenrollStudentFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (student != null && course != null) {
            student.getEnrolledCourses().remove(course);
            studentRepository.save(student);
        }
    }
}
