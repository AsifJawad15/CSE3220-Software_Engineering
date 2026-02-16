package com.example.assignment.controller;

import com.example.assignment.model.Course;
import com.example.assignment.model.Department;
import com.example.assignment.model.Role;
import com.example.assignment.model.Student;
import com.example.assignment.model.Teacher;
import com.example.assignment.service.TeacherService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TeacherController - Handles requests for TEACHER role
 *
 * BASE URL: /teacher/**
 * ACCESS: Only users with ROLE_TEACHER (defined in SecurityConfig)
 *
 * This controller has FULL CRUD operations:
 * - GET methods → READ (list, view, show forms)
 * - POST methods → CREATE and UPDATE (save data)
 * - GET /delete → DELETE (remove data)
 *
 * @RequestMapping("/teacher") - All methods start with /teacher
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEACHER PROFILE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Show edit profile form - GET /teacher/profile
     */
    @GetMapping("/profile")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Teacher teacher = teacherService.getTeacherByUsername(userDetails.getUsername());
        model.addAttribute("teacher", teacher);
        model.addAttribute("departments", teacherService.getAllDepartments());
        return "teacher/profile_form";
    }

    /**
     * Save profile changes - POST /teacher/profile
     *
     * @ModelAttribute Teacher teacher - Spring automatically fills Teacher object
     *                                   from form fields
     */
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute Teacher teacher) {
        teacherService.updateTeacher(teacher);
        return "redirect:/dashboard";
    }

    // ═══════════════════════════════════════════════════════════════════
    // STUDENT MANAGEMENT (CRUD)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * READ - List all students - GET /teacher/students
     */
    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", teacherService.getAllStudents());
        return "teacher/students";
    }

    /**
     * CREATE - Show new student form - GET /teacher/students/new
     */
    @GetMapping("/students/new")
    public String createStudentForm(Model model) {
        Student student = new Student();
        student.setRole(Role.STUDENT);  // Pre-set the role
        model.addAttribute("student", student);
        model.addAttribute("departments", teacherService.getAllDepartments());
        return "teacher/student_form";
    }

    /**
     * CREATE/UPDATE - Save student - POST /teacher/students
     *
     * @ModelAttribute Student student - Form data converted to Student object
     * @RequestParam departmentId - The selected department ID from dropdown
     */
    @PostMapping("/students")
    public String saveStudent(@ModelAttribute Student student,
                              @RequestParam(required = false) Long departmentId) {
        student.setRole(Role.STUDENT);
        if (departmentId != null) {
            Department department = teacherService.getDepartmentById(departmentId);
            student.setDepartment(department);
        }
        teacherService.saveStudent(student);
        return "redirect:/teacher/students";
    }

    /**
     * UPDATE - Show edit student form - GET /teacher/students/edit/{id}
     *
     * @PathVariable Long id - Gets the {id} from the URL
     * Example: /teacher/students/edit/5 → id = 5
     */
    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", teacherService.getStudentById(id));
        model.addAttribute("departments", teacherService.getAllDepartments());
        return "teacher/student_form";
    }

    /**
     * DELETE - Remove student - GET /teacher/students/delete/{id}
     */
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        teacherService.deleteStudent(id);
        return "redirect:/teacher/students";
    }

    /**
     * Manage student course enrollments - GET /teacher/students/{id}/enrollments
     */
    @GetMapping("/students/{id}/enrollments")
    public String manageEnrollments(@PathVariable Long id, Model model) {
        Student student = teacherService.getStudentById(id);
        if (student == null) {
            return "redirect:/teacher/students";
        }

        List<Course> allCourses = teacherService.getAllCourses();
        List<Course> enrolledCourses = new ArrayList<>(student.getEnrolledCourses());

        // Filter out already enrolled courses
        List<Course> availableCourses = allCourses.stream()
            .filter(course -> !enrolledCourses.contains(course))
            .collect(java.util.stream.Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("enrolledCourses", enrolledCourses);
        model.addAttribute("availableCourses", availableCourses);
        return "teacher/student_enrollments";
    }

    /**
     * Enroll student in course - POST /teacher/students/{studentId}/enroll/{courseId}
     */
    @PostMapping("/students/{studentId}/enroll/{courseId}")
    public String enrollStudent(@PathVariable Long studentId, @PathVariable Long courseId) {
        teacherService.enrollStudentInCourse(studentId, courseId);
        return "redirect:/teacher/students/" + studentId + "/enrollments";
    }

    /**
     * Unenroll student from course - POST /teacher/students/{studentId}/unenroll/{courseId}
     */
    @PostMapping("/students/{studentId}/unenroll/{courseId}")
    public String unenrollStudent(@PathVariable Long studentId, @PathVariable Long courseId) {
        teacherService.unenrollStudentFromCourse(studentId, courseId);
        return "redirect:/teacher/students/" + studentId + "/enrollments";
    }

    // ═══════════════════════════════════════════════════════════════════
    // DEPARTMENT MANAGEMENT (CRUD)
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", teacherService.getAllDepartments());
        return "teacher/departments";
    }

    @GetMapping("/departments/new")
    public String createDepartmentForm(Model model) {
        model.addAttribute("department", new Department());
        return "teacher/department_form";
    }

    @PostMapping("/departments")
    public String saveDepartment(@ModelAttribute Department department) {
        teacherService.saveDepartment(department);
        return "redirect:/teacher/departments";
    }

    @GetMapping("/departments/edit/{id}")
    public String editDepartmentForm(@PathVariable Long id, Model model) {
        model.addAttribute("department", teacherService.getDepartmentById(id));
        return "teacher/department_form";
    }

    @GetMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        teacherService.deleteDepartment(id);
        return "redirect:/teacher/departments";
    }

    // ═══════════════════════════════════════════════════════════════════
    // COURSE MANAGEMENT (CRUD)
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", teacherService.getAllCourses());
        return "teacher/courses";
    }

    @GetMapping("/courses/new")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teacher/course_form";
    }

    @PostMapping("/courses")
    public String saveCourse(@ModelAttribute Course course,
                             @RequestParam(required = false) Long teacherId) {
        if (teacherId != null) {
            Teacher teacher = teacherService.getTeacherById(teacherId);
            course.setTeacher(teacher);
        }
        teacherService.saveCourse(course);
        return "redirect:/teacher/courses";
    }

    @GetMapping("/courses/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", teacherService.getCourseById(id));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "teacher/course_form";
    }

    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        teacherService.deleteCourse(id);
        return "redirect:/teacher/courses";
    }
}
