package com.marketplace.minimarketplace.service;
import com.marketplace.minimarketplace.dto.request.LoginRequest;
import com.marketplace.minimarketplace.dto.request.RegisterRequest;
import com.marketplace.minimarketplace.dto.response.AuthResponse;
import com.marketplace.minimarketplace.entity.User;
import com.marketplace.minimarketplace.entity.UserProfile;
import com.marketplace.minimarketplace.exception.DuplicateResourceException;
import com.marketplace.minimarketplace.exception.UnauthorizedException;
import com.marketplace.minimarketplace.mapper.UserMapper;
import com.marketplace.minimarketplace.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; this.jwtService = jwtService;
    }
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        User user = User.builder().name(request.getName()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role("ROLE_USER").build();
        UserProfile profile = UserProfile.builder().user(user).phone(request.getPhone()).address(request.getAddress()).build();
        user.setProfile(profile);
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return UserMapper.toAuthResponse(user, token);
    }
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) throw new UnauthorizedException("Invalid email or password");
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return UserMapper.toAuthResponse(user, token);
    }
}