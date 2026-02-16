package com.example.assignment.service;

import com.example.assignment.model.Role;
import com.example.assignment.model.Student;
import com.example.assignment.model.Teacher;
import com.example.assignment.repository.StudentRepository;
import com.example.assignment.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * CustomUserDetailsServiceTest - AUTHENTICATION TESTS
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHAT IS CustomUserDetailsService?
 * - Implements Spring Security's UserDetailsService interface
 * - Called by Spring Security during login to load user data
 * - Converts our User entity to Spring Security's UserDetails
 *
 * WHY IS THIS IMPORTANT FOR AUTHENTICATION?
 * - This is WHERE Spring Security gets user info from database
 * - If this fails, user cannot log in
 * - Roles are assigned here (ROLE_STUDENT or ROLE_TEACHER)
 *
 * AUTHENTICATION FLOW:
 * 1. User enters username/password on login form
 * 2. Spring Security calls loadUserByUsername(username)
 * 3. This service finds user in database
 * 4. Returns UserDetails with username, password, roles
 * 5. Spring Security compares passwords
 * 6. If match → Login success, if not → Login failed
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomUserDetailsService - Authentication Tests")
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Student testStudent;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        // Create a student user
        testStudent = new Student();
        testStudent.setUsername("authstudent");
        testStudent.setPassword(passwordEncoder.encode("studentpass"));
        testStudent.setRole(Role.STUDENT);
        testStudent.setFirstName("Auth");
        testStudent.setLastName("Student");
        testStudent = studentRepository.save(testStudent);

        // Create a teacher user
        testTeacher = new Teacher();
        testTeacher.setUsername("authteacher");
        testTeacher.setPassword(passwordEncoder.encode("teacherpass"));
        testTeacher.setRole(Role.TEACHER);
        testTeacher.setFirstName("Auth");
        testTeacher.setLastName("Teacher");
        testTeacher = teacherRepository.save(testTeacher);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SUCCESSFUL AUTHENTICATION TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("loadUserByUsername - Should return UserDetails for STUDENT")
    void loadUserByUsername_StudentUser_ReturnsUserDetails() {
        // ACT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("authstudent");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("authstudent", userDetails.getUsername());

        // Verify user has ROLE_STUDENT authority
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    @DisplayName("loadUserByUsername - Should return UserDetails for TEACHER")
    void loadUserByUsername_TeacherUser_ReturnsUserDetails() {
        // ACT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("authteacher");

        // ASSERT
        assertNotNull(userDetails);
        assertEquals("authteacher", userDetails.getUsername());

        // Verify user has ROLE_TEACHER authority
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_TEACHER")));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FAILED AUTHENTICATION TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("loadUserByUsername - Should throw exception when user not found")
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonexistent")
        );

        // Verify exception message
        assertTrue(exception.getMessage().contains("nonexistent"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ROLE-BASED AUTHENTICATION TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("loadUserByUsername - STUDENT should NOT have TEACHER role")
    void loadUserByUsername_Student_ShouldNotHaveTeacherRole() {
        // ACT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("authstudent");

        // ASSERT - Student should NOT have TEACHER role
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_TEACHER")));

        // Student SHOULD have STUDENT role
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    @DisplayName("loadUserByUsername - TEACHER should NOT have STUDENT role")
    void loadUserByUsername_Teacher_ShouldNotHaveStudentRole() {
        // ACT
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("authteacher");

        // ASSERT - Teacher should NOT have STUDENT role
        assertFalse(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT")));

        // Teacher SHOULD have TEACHER role
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_TEACHER")));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ROLE ASSIGNMENT DOCUMENTATION TEST
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Authentication Flow - Roles are correctly assigned from database")
    void authenticationFlow_RolesCorrectlyAssigned() {
        // This test documents how authentication works:

        // 1. User enters credentials on /login page
        // 2. Spring Security calls loadUserByUsername()
        // 3. We find user in database with their role (STUDENT or TEACHER)
        // 4. We create UserDetails with .roles(user.getRole().name())
        // 5. Spring automatically adds "ROLE_" prefix
        //    - "STUDENT" becomes "ROLE_STUDENT"
        //    - "TEACHER" becomes "ROLE_TEACHER"
        // 6. These roles are used in SecurityConfig for authorization:
        //    - /student/** requires ROLE_STUDENT
        //    - /teacher/** requires ROLE_TEACHER

        UserDetails student = customUserDetailsService.loadUserByUsername("authstudent");
        UserDetails teacher = customUserDetailsService.loadUserByUsername("authteacher");

        // Both have exactly one authority (their role)
        assertEquals(1, student.getAuthorities().size());
        assertEquals(1, teacher.getAuthorities().size());
    }
}
