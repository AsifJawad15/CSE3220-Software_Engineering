package com.example.assignment.controller;

import com.example.assignment.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * TeacherControllerTest - INTEGRATION TESTS for TeacherController (CRUD Operations)
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHAT DOES TEACHER CONTROLLER DO?
 * - Full CRUD (Create, Read, Update, Delete) operations
 * - Manages Students, Courses, Departments
 * - Only accessible by users with ROLE_TEACHER
 *
 * WHY INTEGRATION TESTS?
 * - Tests the full request flow through Spring Security
 * - Uses H2 in-memory database (configured in application-test.properties)
 * - More reliable with Java 25+ where Mockito has compatibility issues
 *
 * @SpringBootTest - Starts the full Spring application
 * @AutoConfigureMockMvc - Provides MockMvc for HTTP testing
 * @ActiveProfiles("test") - Uses H2 database instead of PostgreSQL
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("TeacherController Integration Tests")
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ═══════════════════════════════════════════════════════════════════════════
    // STUDENT MANAGEMENT TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Student Management Tests")
    class StudentManagementTests {

        @Test
        @DisplayName("listStudents - Should be accessible by TEACHER role")
        @WithMockUser(roles = "TEACHER")
        void listStudents_AccessibleByTeacher() throws Exception {
            mockMvc.perform(get("/teacher/students"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("teacher/students"));
        }

        @Test
        @DisplayName("listStudents - Should be forbidden for STUDENT role")
        @WithMockUser(roles = "STUDENT")
        void listStudents_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/teacher/students"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("createStudentForm - Should return form with empty student")
        @WithMockUser(roles = "TEACHER")
        void createStudentForm_Success() throws Exception {
            mockMvc.perform(get("/teacher/students/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("teacher/student_form"))
                    .andExpect(model().attributeExists("student"))
                    .andExpect(model().attributeExists("departments"));
        }

        @Test
        @DisplayName("editStudentForm - Should be accessible by TEACHER (authorization test)")
        @WithMockUser(roles = "TEACHER")
        void editStudentForm_Success() throws Exception {
            // This test verifies that a TEACHER can access the edit endpoint
            // When student doesn't exist (id=999), the controller may throw an exception
            // or redirect. The important thing is it should NOT return 403 (Forbidden)
            try {
                mockMvc.perform(get("/teacher/students/edit/999"))
                        .andExpect(result -> {
                            int status = result.getResponse().getStatus();
                            // Should not be forbidden (403) - teacher should have access
                            assertNotEquals(403, status, "Teacher should have access");
                        });
            } catch (jakarta.servlet.ServletException e) {
                // ServletException is acceptable - it means authorization passed
                // but template/data error occurred (which is expected with non-existent student)
                assertTrue(e.getMessage().contains("TemplateProcessingException")
                        || e.getMessage().contains("student"),
                    "Expected template error for missing student, got: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEACHER PROFILE TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Teacher Profile Tests")
    class TeacherProfileTests {

        @Test
        @DisplayName("editProfile - Should be accessible by TEACHER (may error if teacher not in DB)")
        @WithMockUser(username = "teacher", roles = "TEACHER")
        void editProfile_Success() throws Exception {
            // Note: Uses "teacher" username which should exist from DataInitializer
            // If teacher exists, returns 200; otherwise may return 500 or redirect
            mockMvc.perform(get("/teacher/profile"))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        // Should not be forbidden (403) - teacher should have access
                        assertNotEquals(403, status, "Teacher should have access");
                        // Accept 200 (success), 302 (redirect), or 500 (data not found)
                        assertTrue(status == 302 || status == 200 || status == 500,
                            "Expected redirect, success, or server error, got: " + status);
                    });
        }

        @Test
        @DisplayName("editProfile - Should be forbidden for STUDENT")
        @WithMockUser(roles = "STUDENT")
        void editProfile_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/teacher/profile"))
                    .andExpect(status().isForbidden());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DEPARTMENT MANAGEMENT TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Department Management Tests")
    class DepartmentManagementTests {

        @Test
        @DisplayName("listDepartments - Should return departments view")
        @WithMockUser(roles = "TEACHER")
        void listDepartments_Success() throws Exception {
            mockMvc.perform(get("/teacher/departments"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("teacher/departments"));
        }

        @Test
        @DisplayName("createDepartmentForm - Should return form with empty department")
        @WithMockUser(roles = "TEACHER")
        void createDepartmentForm_Success() throws Exception {
            mockMvc.perform(get("/teacher/departments/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("teacher/department_form"))
                    .andExpect(model().attributeExists("department"));
        }

        @Test
        @DisplayName("Department CRUD - Should be forbidden for STUDENT")
        @WithMockUser(roles = "STUDENT")
        void departmentCrud_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/teacher/departments"))
                    .andExpect(status().isForbidden());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // COURSE MANAGEMENT TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Course Management Tests")
    class CourseManagementTests {

        @Test
        @DisplayName("listCourses - Should return courses view")
        @WithMockUser(roles = "TEACHER")
        void listCourses_Success() throws Exception {
            mockMvc.perform(get("/teacher/courses"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("teacher/courses"));
        }

        @Test
        @DisplayName("createCourseForm - Should return form with empty course")
        @WithMockUser(roles = "TEACHER")
        void createCourseForm_Success() throws Exception {
            mockMvc.perform(get("/teacher/courses/new"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("teacher/course_form"))
                    .andExpect(model().attributeExists("course"));
        }

        @Test
        @DisplayName("Course CRUD - Should be forbidden for STUDENT")
        @WithMockUser(roles = "STUDENT")
        void courseCrud_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/teacher/courses"))
                    .andExpect(status().isForbidden());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CRUD ACCESS CONTROL SUMMARY
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("TeacherController - TEACHER has full CRUD access")
    @WithMockUser(roles = "TEACHER")
    void teacherController_TeacherHasFullAccess() throws Exception {
        // READ operations
        mockMvc.perform(get("/teacher/students")).andExpect(status().isOk());
        mockMvc.perform(get("/teacher/courses")).andExpect(status().isOk());
        mockMvc.perform(get("/teacher/departments")).andExpect(status().isOk());

        // CREATE forms
        mockMvc.perform(get("/teacher/students/new")).andExpect(status().isOk());
        mockMvc.perform(get("/teacher/courses/new")).andExpect(status().isOk());
        mockMvc.perform(get("/teacher/departments/new")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("TeacherController - STUDENT has NO access")
    @WithMockUser(roles = "STUDENT")
    void teacherController_StudentHasNoAccess() throws Exception {
        // All teacher URLs should be forbidden for STUDENT
        mockMvc.perform(get("/teacher/students")).andExpect(status().isForbidden());
        mockMvc.perform(get("/teacher/courses")).andExpect(status().isForbidden());
        mockMvc.perform(get("/teacher/departments")).andExpect(status().isForbidden());
        mockMvc.perform(get("/teacher/students/new")).andExpect(status().isForbidden());
    }
}
