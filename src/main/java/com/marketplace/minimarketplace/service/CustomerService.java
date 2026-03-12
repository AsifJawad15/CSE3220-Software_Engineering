package com.marketplace.minimarketplace.service;

import com.marketplace.minimarketplace.dto.request.CustomerRequest;
import com.marketplace.minimarketplace.dto.response.CustomerResponse;
import com.marketplace.minimarketplace.entity.User;
import com.marketplace.minimarketplace.entity.UserProfile;
import com.marketplace.minimarketplace.exception.DuplicateResourceException;
import com.marketplace.minimarketplace.exception.ResourceNotFoundException;
import com.marketplace.minimarketplace.mapper.CustomerMapper;
import com.marketplace.minimarketplace.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();
        UserProfile profile = UserProfile.builder()
                .user(user)
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
        user.setProfile(profile);
        User saved = userRepository.save(user);
        return CustomerMapper.toResponse(saved);
    }

    public CustomerResponse getCustomerById(Long id) {
        return CustomerMapper.toResponse(findCustomerOrThrow(id));
    }

    public List<CustomerResponse> getAllCustomers() {
        return userRepository.findAll().stream()
                .map(CustomerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        User user = findCustomerOrThrow(id);

        // If email is changing, check it's not taken by another user
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = UserProfile.builder().user(user).build();
            user.setProfile(profile);
        }
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());

        User saved = userRepository.save(user);
        return CustomerMapper.toResponse(saved);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        User user = findCustomerOrThrow(id);
        userRepository.delete(user);
    }

    private User findCustomerOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}

