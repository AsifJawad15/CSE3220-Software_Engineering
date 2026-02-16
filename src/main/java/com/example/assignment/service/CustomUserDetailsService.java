package com.example.assignment.service;

import com.example.assignment.model.User;
import com.example.assignment.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService - Loads user data for Spring Security
 *
 * IMPLEMENTS UserDetailsService:
 * This is a Spring Security interface. When someone tries to login,
 * Spring Security calls loadUserByUsername() to get user details.
 *
 * FLOW:
 * 1. User enters username & password on login form
 * 2. Spring Security calls loadUserByUsername(username)
 * 3. We find the user in database
 * 4. We return UserDetails object with username, password, roles
 * 5. Spring Security compares passwords and grants/denies access
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor Injection - Spring automatically provides UserRepository
     * This is called "Dependency Injection" - a core Spring concept
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username - Called by Spring Security during login
     *
     * @param username - The username entered in login form
     * @return UserDetails - Spring Security's user representation
     * @throws UsernameNotFoundException - If user doesn't exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Step 1: Find user in database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Step 2: Convert our User to Spring Security's UserDetails
        // .roles() automatically adds "ROLE_" prefix, so "TEACHER" becomes "ROLE_TEACHER"
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())  // "STUDENT" or "TEACHER"
                .build();
    }
}
