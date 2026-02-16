package com.example.assignment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * StudentControllerTest - INTEGRATION TESTS for StudentController
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * Tests role-based access control:
 * - STUDENT can access /student/** URLs
 * - TEACHER cannot access /student/** URLs (403 Forbidden)
 * - Unauthenticated users redirect to login
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("StudentController Integration Tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ═══════════════════════════════════════════════════════════════════════════
    // VIEW COURSES TESTS (courses view works with null data)
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("View Courses Tests")
    class ViewCoursesTests {

        @Test
        @DisplayName("viewCourses - Should return courses view for STUDENT role")
        @WithMockUser(username = "student1", roles = "STUDENT")
        void viewCourses_WithStudentRole_ReturnsCoursesView() throws Exception {
            mockMvc.perform(get("/student/courses"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("student/courses"));
        }

        @Test
        @DisplayName("viewCourses - Should be forbidden for TEACHER role")
        @WithMockUser(roles = "TEACHER")
        void viewCourses_WithTeacherRole_ReturnsForbidden() throws Exception {
            mockMvc.perform(get("/student/courses"))
                    .andExpect(status().isForbidden());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // AUTHORIZATION TESTS (Testing 403 Forbidden for wrong roles)
    // ═══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @DisplayName("Student cannot access Teacher URLs")
        @WithMockUser(roles = "STUDENT")
        void student_CannotAccess_TeacherUrls() throws Exception {
            mockMvc.perform(get("/teacher/students"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/teacher/students/new"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/teacher/courses"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/teacher/departments"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Teacher cannot access Student URLs")
        @WithMockUser(roles = "TEACHER")
        void teacher_CannotAccess_StudentUrls() throws Exception {
            mockMvc.perform(get("/student/profile"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/student/courses"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Unauthenticated user redirects to login")
        void unauthenticated_RedirectsToLogin() throws Exception {
            mockMvc.perform(get("/student/profile"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }
}
