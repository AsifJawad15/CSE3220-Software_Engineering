package com.example.assignment.config;

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
 * SecurityConfigTest - INTEGRATION TESTS for Role-Based Authorization
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHAT IS AN INTEGRATION TEST?
 * - Tests multiple components working TOGETHER
 * - @SpringBootTest starts the entire Spring application
 * - Uses REAL security configuration (not mocked)
 * - Tests actual HTTP requests through security filters
 *
 * WHAT IS @AutoConfigureMockMvc?
 * - Provides MockMvc for making HTTP requests in tests
 * - MockMvc simulates HTTP requests without starting a real server
 *
 * WHAT IS @WithMockUser?
 * - Creates a fake authenticated user for testing
 * - @WithMockUser(roles = "STUDENT") = logged in as a student
 * - @WithMockUser(roles = "TEACHER") = logged in as a teacher
 *
 * WHAT IS @ActiveProfiles("test")?
 * - Uses application-test.properties (H2 database instead of PostgreSQL)
 *
 * SECURITY RULES BEING TESTED (from SecurityConfig.java):
 * - /login, /, /css/** → PUBLIC (anyone can access)
 * - /student/** → Only ROLE_STUDENT
 * - /teacher/** → Only ROLE_TEACHER
 * - Everything else → Must be authenticated
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security Configuration - Role-Based Access Control Tests")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ═══════════════════════════════════════════════════════════════════════════
    // PUBLIC URL TESTS (No Authentication Required)
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Public URLs - No Authentication Required")
    class PublicUrlTests {

        @Test
        @DisplayName("Login page should be accessible without authentication")
        void loginPage_ShouldBeAccessible_WithoutAuthentication() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Root URL should redirect to login")
        void rootUrl_ShouldRedirect_ToLogin() throws Exception {
            mockMvc.perform(get("/"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @DisplayName("CSS files should be accessible without authentication")
        void cssFiles_ShouldBeAccessible_WithoutAuthentication() throws Exception {
            mockMvc.perform(get("/css/styles.css"))
                    .andExpect(status().isOk());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STUDENT ACCESS CONTROL TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Student URLs - ROLE_STUDENT Required")
    class StudentAccessTests {

        @Test
        @DisplayName("Student courses should be accessible by STUDENT role")
        @WithMockUser(roles = "STUDENT")
        void studentCourses_ShouldBeAccessible_ByStudent() throws Exception {
            mockMvc.perform(get("/student/courses"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Student URLs should return 403 for TEACHER role")
        @WithMockUser(roles = "TEACHER")
        void studentUrls_ShouldBeForbidden_ForTeacher() throws Exception {
            mockMvc.perform(get("/student/profile"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Student URLs should redirect to login for unauthenticated users")
        void studentUrls_ShouldRedirectToLogin_ForUnauthenticated() throws Exception {
            mockMvc.perform(get("/student/profile"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TEACHER ACCESS CONTROL TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Teacher URLs - ROLE_TEACHER Required")
    class TeacherAccessTests {

        @Test
        @DisplayName("Teacher students list should be accessible by TEACHER role")
        @WithMockUser(roles = "TEACHER")
        void teacherStudents_ShouldBeAccessible_ByTeacher() throws Exception {
            mockMvc.perform(get("/teacher/students"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Teacher courses list should be accessible by TEACHER role")
        @WithMockUser(roles = "TEACHER")
        void teacherCourses_ShouldBeAccessible_ByTeacher() throws Exception {
            mockMvc.perform(get("/teacher/courses"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Teacher departments list should be accessible by TEACHER role")
        @WithMockUser(roles = "TEACHER")
        void teacherDepartments_ShouldBeAccessible_ByTeacher() throws Exception {
            mockMvc.perform(get("/teacher/departments"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Teacher URLs should return 403 for STUDENT role")
        @WithMockUser(roles = "STUDENT")
        void teacherUrls_ShouldBeForbidden_ForStudent() throws Exception {
            mockMvc.perform(get("/teacher/students"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Teacher URLs should redirect to login for unauthenticated users")
        void teacherUrls_ShouldRedirectToLogin_ForUnauthenticated() throws Exception {
            mockMvc.perform(get("/teacher/students"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DASHBOARD ACCESS TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Dashboard - Any Authenticated User")
    class DashboardAccessTests {

        @Test
        @DisplayName("Dashboard should be accessible by STUDENT")
        @WithMockUser(roles = "STUDENT")
        void dashboard_ShouldBeAccessible_ByStudent() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Dashboard should be accessible by TEACHER")
        @WithMockUser(roles = "TEACHER")
        void dashboard_ShouldBeAccessible_ByTeacher() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Dashboard should redirect to login for unauthenticated users")
        void dashboard_ShouldRedirectToLogin_ForUnauthenticated() throws Exception {
            mockMvc.perform(get("/dashboard"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CRUD OPERATIONS ACCESS TESTS (Teacher Only)
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("CRUD Operations - Teacher Only")
    class CRUDAccessTests {

        @Test
        @DisplayName("Create student form should be accessible by TEACHER")
        @WithMockUser(roles = "TEACHER")
        void createStudentForm_ShouldBeAccessible_ByTeacher() throws Exception {
            mockMvc.perform(get("/teacher/students/new"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Create student form should be forbidden for STUDENT")
        @WithMockUser(roles = "STUDENT")
        void createStudentForm_ShouldBeForbidden_ForStudent() throws Exception {
            mockMvc.perform(get("/teacher/students/new"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Delete student should be forbidden for STUDENT")
        @WithMockUser(roles = "STUDENT")
        void deleteStudent_ShouldBeForbidden_ForStudent() throws Exception {
            mockMvc.perform(get("/teacher/students/delete/1"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Create course form should be forbidden for STUDENT")
        @WithMockUser(roles = "STUDENT")
        void createCourseForm_ShouldBeForbidden_ForStudent() throws Exception {
            mockMvc.perform(get("/teacher/courses/new"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Create department form should be forbidden for STUDENT")
        @WithMockUser(roles = "STUDENT")
        void createDepartmentForm_ShouldBeForbidden_ForStudent() throws Exception {
            mockMvc.perform(get("/teacher/departments/new"))
                    .andExpect(status().isForbidden());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AUTHORIZATION SUMMARY TEST
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Authorization Summary - Document all security rules")
    void authorizationSummary_DocumentSecurityRules() throws Exception {
        /*
         * ═══════════════════════════════════════════════════════════════════════
         * SECURITY RULES SUMMARY (SecurityConfig.java)
         * ═══════════════════════════════════════════════════════════════════════
         *
         * PUBLIC (permitAll):
         * - GET /login          → Login page
         * - GET /               → Redirects to /login
         * - GET /css/**         → Static CSS files
         * - GET /js/**          → Static JavaScript files
         * - GET /register       → Registration page (if exists)
         *
         * STUDENT ONLY (.hasRole("STUDENT")):
         * - GET /student/profile    → View own profile (READ)
         * - GET /student/courses    → View enrolled courses (READ)
         *
         * TEACHER ONLY (.hasRole("TEACHER")):
         * - GET  /teacher/students      → List all students (READ)
         * - GET  /teacher/students/new  → Create student form (CREATE)
         * - POST /teacher/students      → Save student (CREATE/UPDATE)
         * - GET  /teacher/students/edit/{id} → Edit student form (UPDATE)
         * - GET  /teacher/students/delete/{id} → Delete student (DELETE)
         * - ... same pattern for courses and departments
         *
         * AUTHENTICATED (.anyRequest().authenticated()):
         * - GET /dashboard → Main dashboard (both roles)
         *
         * ═══════════════════════════════════════════════════════════════════════
         * KEY SECURITY PRINCIPLE:
         * - STUDENT = READ ONLY (can only view their own data)
         * - TEACHER = FULL CRUD (can manage all data)
         * ═══════════════════════════════════════════════════════════════════════
         */

        // This test serves as documentation
        // All actual security rules are tested in nested classes above
    }
}
