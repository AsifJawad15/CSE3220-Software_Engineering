package com.example.assignment.controller;

import com.example.assignment.model.Student;
import com.example.assignment.service.StudentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * StudentController - Handles requests for STUDENT role
 *
 * BASE URL: /student/**
 * ACCESS: Only users with ROLE_STUDENT (defined in SecurityConfig)
 *
 * IMPORTANT: This controller only has GET methods (READ operations)
 * Students CANNOT create, update, or delete anything!
 *
 * @RequestMapping("/student") - All methods in this class start with /student
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * View own profile - GET /student/profile
     *
     * @AuthenticationPrincipal UserDetails userDetails
     * - This automatically gets the currently logged-in user's info
     * - We use the username to find the student's full details
     *
     * Model model - Used to pass data to the HTML template
     * - model.addAttribute("student", student) makes 'student' available in HTML
     *
     * return "student/profile" - Looks for templates/student/profile.html
     */
    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get the logged-in student's username
        String username = userDetails.getUsername();

        // Find the student in database
        Student student = studentService.getStudentByUsername(username);

        // Pass student data to the HTML template
        model.addAttribute("student", student);

        // Return the view name (templates/student/profile.html)
        return "student/profile";
    }

    /**
     * View enrolled courses - GET /student/courses
     *
     * Shows only the courses this student is enrolled in
     */
    @GetMapping("/courses")
    public String viewCourses(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Student student = studentService.getStudentByUsername(userDetails.getUsername());
        if (student != null) {
            // Pass the student's courses to template
            model.addAttribute("courses", student.getEnrolledCourses());
        }
        return "student/courses";
    }
}
