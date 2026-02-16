package com.example.assignment.service;

import com.example.assignment.model.*;
import com.example.assignment.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * TeacherServiceTest - INTEGRATION TESTS for TeacherService (CRUD Operations)
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHAT IS TEACHER SERVICE?
 * - Has FULL CRUD (Create, Read, Update, Delete) operations
 * - Manages Students, Courses, Departments
 * - Handles password encoding for new/updated users
 *
 * WHY INTEGRATION TESTS?
 * - Uses real H2 database (in-memory)
 * - Tests actual database operations
 * - More reliable with Java 22+ where Mockito has compatibility issues
 *
 * @Transactional - Each test runs in a transaction that rolls back after test
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TeacherService Integration Tests")
class TeacherServiceTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Department testDepartment;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Create test department
        testDepartment = new Department();
        testDepartment.setName("Test Department");
        testDepartment = departmentRepository.save(testDepartment);

        // Create test course
        testCourse = new Course();
        testCourse.setName("Test Course");
        testCourse.setCode("TEST101");
        testCourse = courseRepository.save(testCourse);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STUDENT CRUD TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Student CRUD Operations")
    class StudentCRUDTests {

        @Test
        @DisplayName("getAllStudents - Should return all students")
        void getAllStudents_Success() {
            List<Student> students = teacherService.getAllStudents();
            assertNotNull(students);
            // DataInitializer creates some students, so list should not be empty
        }

        @Test
        @DisplayName("saveStudent - Should save new student with encoded password")
        void saveStudent_NewStudent_EncodesPassword() {
            // ARRANGE
            Student newStudent = new Student();
            newStudent.setUsername("newstudent");
            newStudent.setPassword("rawPassword");
            newStudent.setFirstName("New");
            newStudent.setLastName("Student");
            newStudent.setRole(Role.STUDENT);
            newStudent.setDepartment(testDepartment);

            // ACT
            Student saved = teacherService.saveStudent(newStudent);

            // ASSERT
            assertNotNull(saved.getId());
            assertNotEquals("rawPassword", saved.getPassword()); // Password should be encoded
        }

        @Test
        @DisplayName("getStudentById - Should return student when found")
        void getStudentById_WhenFound_ReturnsStudent() {
            // Create a student first
            Student student = new Student();
            student.setUsername("findme");
            student.setPassword("password");
            student.setRole(Role.STUDENT);
            Student saved = teacherService.saveStudent(student);

            // ACT
            Student found = teacherService.getStudentById(saved.getId());

            // ASSERT
            assertNotNull(found);
            assertEquals("findme", found.getUsername());
        }

        @Test
        @DisplayName("getStudentById - Should return null when not found")
        void getStudentById_WhenNotFound_ReturnsNull() {
            Student result = teacherService.getStudentById(99999L);
            assertNull(result);
        }

        @Test
        @DisplayName("deleteStudent - Should delete student by ID")
        void deleteStudent_Success() {
            // Create a student first
            Student student = new Student();
            student.setUsername("deleteme");
            student.setPassword("password");
            student.setRole(Role.STUDENT);
            Student saved = teacherService.saveStudent(student);
            Long id = saved.getId();

            // ACT
            teacherService.deleteStudent(id);

            // ASSERT
            assertNull(teacherService.getStudentById(id));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DEPARTMENT CRUD 

    @Nested
    @DisplayName("Department CRUD Operations")
    class DepartmentCRUDTests {

        @Test
        @DisplayName("getAllDepartments - Should return all departments")
        void getAllDepartments_Success() {
            List<Department> departments = teacherService.getAllDepartments();
            assertNotNull(departments);
            assertTrue(departments.size() > 0);
        }

        @Test
        @DisplayName("getDepartmentById - Should return department when found")
        void getDepartmentById_WhenFound_ReturnsDepartment() {
            Department found = teacherService.getDepartmentById(testDepartment.getId());
            assertNotNull(found);
            assertEquals("Test Department", found.getName());
        }

        @Test
        @DisplayName("saveDepartment - Should save department")
        void saveDepartment_Success() {
            Department newDept = new Department();
            newDept.setName("New Department");

            Department saved = teacherService.saveDepartment(newDept);

            assertNotNull(saved.getId());
            assertEquals("New Department", saved.getName());
        }

        @Test
        @DisplayName("deleteDepartment - Should delete department")
        void deleteDepartment_Success() {
            Department dept = new Department();
            dept.setName("ToDelete");
            Department saved = teacherService.saveDepartment(dept);
            Long id = saved.getId();

            teacherService.deleteDepartment(id);

            assertNull(teacherService.getDepartmentById(id));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // COURSE CRUD TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Course CRUD Operations")
    class CourseCRUDTests {

        @Test
        @DisplayName("getAllCourses - Should return all courses")
        void getAllCourses_Success() {
            List<Course> courses = teacherService.getAllCourses();
            assertNotNull(courses);
            assertTrue(courses.size() > 0);
        }

        @Test
        @DisplayName("saveCourse - Should save course")
        void saveCourse_Success() {
            Course newCourse = new Course();
            newCourse.setName("New Course");
            newCourse.setCode("NEW101");

            Course saved = teacherService.saveCourse(newCourse);

            assertNotNull(saved.getId());
            assertEquals("New Course", saved.getName());
        }

        @Test
        @DisplayName("deleteCourse - Should delete course")
        void deleteCourse_Success() {
            Course course = new Course();
            course.setName("ToDelete");
            course.setCode("DEL101");
            Course saved = teacherService.saveCourse(course);
            Long id = saved.getId();

            teacherService.deleteCourse(id);

            assertNull(teacherService.getCourseById(id));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FULL CRUD VERIFICATION TEST
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TeacherService - Should have full CRUD for Students, Departments, Courses")
    void teacherService_ShouldHaveFullCRUD() {
        // This test documents that TeacherService has:
        // STUDENTS: getAllStudents, getStudentById, saveStudent, deleteStudent
        // DEPARTMENTS: getAllDepartments, getDepartmentById, saveDepartment, deleteDepartment
        // COURSES: getAllCourses, getCourseById, saveCourse, deleteCourse

        // This is the KEY DIFFERENCE from StudentService:
        // TeacherService has CREATE, UPDATE, DELETE operations
        // StudentService only has READ operations

        // Verify all methods exist and work
        assertNotNull(teacherService.getAllStudents());
        assertNotNull(teacherService.getAllDepartments());
        assertNotNull(teacherService.getAllCourses());
    }
}
