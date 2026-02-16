package com.example.assignment.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * AuthControllerTest - UNIT TESTS for AuthController
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHAT DOES AuthController DO?
 * - Handles login page display
 * - Handles root URL redirect
 * - Handles dashboard display after login
 *
 * WHY IS THIS SIMPLE?
 * - AuthController has no dependencies (no @Mock needed)
 * - It just returns view names
 * - The actual authentication logic is in Spring Security (SecurityConfig)
 * ═══════════════════════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    // ═══════════════════════════════════════════════════════════════════════════
    // ROOT URL TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("root - Should redirect to login page")
    void root_ShouldRedirectToLogin() {
        // ACT
        String result = authController.root();

        // ASSERT
        assertEquals("redirect:/login", result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LOGIN PAGE TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("login - Should return login view")
    void login_ShouldReturnLoginView() {
        // ACT
        String result = authController.login();

        // ASSERT
        assertEquals("login", result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DASHBOARD TESTS
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    @DisplayName("dashboard - Should return dashboard view")
    void dashboard_ShouldReturnDashboardView() {
        // ACT
        String result = authController.dashboard();

        // ASSERT
        assertEquals("dashboard", result);
    }
}
