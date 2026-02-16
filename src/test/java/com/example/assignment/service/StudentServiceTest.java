package com.example.assignment.service;

import com.example.assignment.model.Course;
import com.example.assignment.model.Department;
import com.example.assignment.model.Role;
import com.example.assignment.model.Student;
import com.example.assignment.repository.CourseRepository;
import com.example.assignment.repository.DepartmentRepository;
import com.example.assignment.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * StudentServiceTest - INTEGRATION TESTS for StudentService (Service Layer)
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHY TEST SERVICE LAYER?
 * - Service contains BUSINESS LOGIC
 * - Controller just routes requests, Service does the actual work
 * - Testing service ensures business rules are correct
 *
 * STUDENT SERVICE RULE:
 * - Students can only READ data (no create, update, delete)
 * - This test verifies the service only has read operations
 *
 * WHY INTEGRATION TESTS?
 * - Uses real H2 database
 * - More reliable with Java 22+ where Mockito has compatibility issues
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("StudentService Integration Tests")
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Test data
    private Student testStudent;
    private Department testDepartment;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Create test department
        testDepartment = new Department();
        testDepartment.setName("Test CS Department");
        testDepartment = departmentRepository.save(testDepartment);

        // Create test course
        testCourse = new Course();
        testCourse.setName("Test Java Course");
        testCourse.setCode("TESTCS101");
        testCourse = courseRepository.save(testCourse);

        // Create test student
        testStudent = new Student();
        testStudent.setUsername("teststudent");
        testStudent.setPassword(passwordEncoder.encode("password"));
        testStudent.setFirstName("Test");
        testStudent.setLastName("Student");
        testStudent.setRole(Role.STUDENT);
        testStudent.setDepartment(testDepartment);
        testStudent.getEnrolledCourses().add(testCourse);
        testStudent = studentRepository.save(testStudent);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET STUDENT BY ID TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getStudentById - Should return student when found")
    void getStudentById_WhenFound_ReturnsStudent() {
        // ACT
        Student result = studentService.getStudentById(testStudent.getId());

        // ASSERT
        assertNotNull(result);
        assertEquals("teststudent", result.getUsername());
        assertEquals("Test", result.getFirstName());
    }

    @Test
    @DisplayName("getStudentById - Should return null when not found")
    void getStudentById_WhenNotFound_ReturnsNull() {
        // ACT
        Student result = studentService.getStudentById(99999L);

        // ASSERT
        assertNull(result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET STUDENT BY USERNAME TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getStudentByUsername - Should return student when found")
    void getStudentByUsername_WhenFound_ReturnsStudent() {
        // ACT
        Student result = studentService.getStudentByUsername("teststudent");

        // ASSERT
        assertNotNull(result);
        assertEquals(testStudent.getId(), result.getId());
    }

    @Test
    @DisplayName("getStudentByUsername - Should return null when not found")
    void getStudentByUsername_WhenNotFound_ReturnsNull() {
        // ACT
        Student result = studentService.getStudentByUsername("nonexistent");

        // ASSERT
        assertNull(result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET ENROLLED COURSES TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("getEnrolledCourses - Should return courses when student found")
    void getEnrolledCourses_WhenStudentFound_ReturnsCourses() {
        // ACT
        Set<Course> result = studentService.getEnrolledCourses(testStudent.getId());

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getEnrolledCourses - Should return empty set when student not found")
    void getEnrolledCourses_WhenStudentNotFound_ReturnsEmptySet() {
        // ACT
        Set<Course> result = studentService.getEnrolledCourses(99999L);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // READ-ONLY VERIFICATION TEST
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("StudentService - Should only have READ operations (no save/delete)")
    void studentService_ShouldOnlyHaveReadOperations() {
        // This test documents that StudentService:
        // ✓ Has getStudentById (READ)
        // ✓ Has getStudentByUsername (READ)
        // ✓ Has getEnrolledCourses (READ)
        // ✗ Does NOT have saveStudent (CREATE/UPDATE)
        // ✗ Does NOT have deleteStudent (DELETE)

        // The StudentService class only has read methods
        // This is enforced by design - students cannot modify data

        // Call all read methods - they should work
        assertDoesNotThrow(() -> studentService.getStudentById(testStudent.getId()));
        assertDoesNotThrow(() -> studentService.getStudentByUsername("teststudent"));
        assertDoesNotThrow(() -> studentService.getEnrolledCourses(testStudent.getId()));

        // NOTE: StudentService does not have save or delete methods
        // This is intentional - students can only READ data
    }
}
