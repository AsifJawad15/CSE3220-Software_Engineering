package com.marketplace.minimarketplace.service;

import com.marketplace.minimarketplace.dto.request.LoginRequest;
import com.marketplace.minimarketplace.dto.request.RegisterRequest;
import com.marketplace.minimarketplace.dto.response.AuthResponse;
import com.marketplace.minimarketplace.entity.User;
import com.marketplace.minimarketplace.exception.DuplicateResourceException;
import com.marketplace.minimarketplace.exception.UnauthorizedException;
import com.marketplace.minimarketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, new StubJwtService());
    }

    @Test
    void register_validRequest_returnsTokenAndUserData() {
        RegisterRequest request = new RegisterRequest("Alice", "alice@test.com", "pass123", "017", "Dhaka");
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        AuthResponse response = authService.register(request);

        assertEquals("alice@test.com", response.getEmail());
        assertEquals("ROLE_CUSTOMER", response.getRole());
        assertEquals("TOKEN_alice@test.com_ROLE_CUSTOMER", response.getToken());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("encodedPass", saved.getPassword());
        assertEquals("ROLE_CUSTOMER", saved.getRole());
        assertEquals("017", saved.getProfile().getPhone());
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest("Alice", "alice@test.com", "pass123", "017", "Dhaka");
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    void login_validCredentials_returnsToken() {
        User user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@test.com")
                .password("encodedPass")
                .role("ROLE_CUSTOMER")
                .build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encodedPass")).thenReturn(true);

        AuthResponse response = authService.login(new LoginRequest("alice@test.com", "pass123"));

        assertEquals("alice@test.com", response.getEmail());
        assertEquals("ROLE_CUSTOMER", response.getRole());
        assertEquals("TOKEN_alice@test.com_ROLE_CUSTOMER", response.getToken());
    }

    @Test
    void login_unknownEmail_throwsUnauthorizedException() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("missing@test.com", "pass123")));
    }

    @Test
    void login_invalidPassword_throwsUnauthorizedException() {
        User user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@test.com")
                .password("encodedPass")
                .role("ROLE_CUSTOMER")
                .build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("alice@test.com", "wrong")));
    }

    private static final class StubJwtService extends JwtService {
        @Override
        public String generateToken(String email, String role) {
            return "TOKEN_" + email + "_" + role;
        }
    }
}

