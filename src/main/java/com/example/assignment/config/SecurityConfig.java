package com.example.assignment.config;

import com.example.assignment.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SecurityConfig - THE HEART OF AUTHENTICATION & AUTHORIZATION
 *
 * This class configures:
 * 1. Which URLs are public vs protected
 * 2. Which roles can access which URLs
 * 3. How passwords are encoded
 * 4. Login/Logout behavior
 *
 * @Configuration - Tells Spring "this class contains bean definitions"
 * @EnableWebSecurity - Enables Spring Security for web applications
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * SecurityFilterChain - Defines the security rules
     *
     * @Bean - Creates a Spring-managed object that can be used elsewhere
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for simplicity (in production, keep it enabled!)
            .csrf(AbstractHttpConfigurer::disable)

            // ═══════════════════════════════════════════════════════════
            // AUTHORIZATION RULES - WHO CAN ACCESS WHAT
            // ═══════════════════════════════════════════════════════════
            .authorizeHttpRequests(auth -> auth
                // PUBLIC URLs - Anyone can access (even without login)
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()

                // TEACHER URLs - Only users with ROLE_TEACHER can access
                // This is where CRUD operations happen
                .requestMatchers("/teacher/**").hasRole("TEACHER")

                // STUDENT URLs - Only users with ROLE_STUDENT can access
                // Students can only READ (view profile, view courses)
                .requestMatchers("/student/**").hasRole("STUDENT")

                // Everything else requires login
                .anyRequest().authenticated()
            )

            // ═══════════════════════════════════════════════════════════
            // LOGIN CONFIGURATION
            // ═══════════════════════════════════════════════════════════
            .formLogin(form -> form
                .loginPage("/login")                    // Custom login page URL
                .defaultSuccessUrl("/dashboard", true)  // Where to go after login
                .permitAll()                            // Allow everyone to see login page
            )

            // ═══════════════════════════════════════════════════════════
            // LOGOUT CONFIGURATION
            // ═══════════════════════════════════════════════════════════
            .logout(logout -> logout
                .logoutUrl("/logout")                   // URL to trigger logout
                .logoutSuccessUrl("/login?logout")      // Where to go after logout
                .permitAll()
            );

        return http.build();
    }

    /**
     * AuthenticationManager - Handles the actual authentication process
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
            .userDetailsService(userDetailsService)  // Use our custom user loader
            .passwordEncoder(passwordEncoder());      // Use our password encoder
        return authBuilder.build();
    }

    /**
     * PasswordEncoder - Encrypts and verifies passwords
     *
     * WHY ENCODE PASSWORDS?
     * - Never store plain text passwords in database!
     * - If database is hacked, encrypted passwords are useless to hackers
     *
     * This implementation uses SHA-256 hashing.
     * In production, use BCryptPasswordEncoder instead (more secure).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {

            /**
             * encode() - Converts plain password to encrypted hash
             * Example: "password" → "5e884898da28047d91..."
             */
            @Override
            public String encode(CharSequence rawPassword) {
                if (rawPassword == null) {
                    throw new IllegalArgumentException("Password cannot be null");
                }
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
                    StringBuilder hexString = new StringBuilder();
                    for (byte b : hash) {
                        String hex = Integer.toHexString(0xff & b);
                        if (hex.length() == 1) hexString.append('0');
                        hexString.append(hex);
                    }
                    return hexString.toString();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("SHA-256 algorithm not found", e);
                }
            }

            /**
             * matches() - Compares plain password with stored hash
             * Called during login to verify password
             */
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || encodedPassword == null) {
                    return false;
                }
                return encode(rawPassword).equals(encodedPassword);
            }
        };
    }
}
