package com.example.assignment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthController - Handles authentication-related pages
 *
 * Routes handled:
 * - GET /        → Redirects to login
 * - GET /login   → Shows login page
 * - GET /dashboard → Shows dashboard after login
 *
 * @Controller - Tells Spring this class handles web requests and returns VIEW names
 *               (not JSON data - that would be @RestController)
 */
@Controller
public class AuthController {

    /**
     * Root URL "/" - Redirect to login page
     *
     * @GetMapping("/") - Handles GET requests to "http://localhost:8081/"
     * return "redirect:/login" - Sends browser to /login URL
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    /**
     * Login page
     *
     * return "login" - Looks for templates/login.html
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Dashboard - Main page after login
     *
     * Both students and teachers see this page,
     * but with different options based on their role
     * (handled in the HTML template with Thymeleaf security tags)
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
