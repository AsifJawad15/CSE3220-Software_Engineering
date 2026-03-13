package com.marketplace.minimarketplace.service;

import com.marketplace.minimarketplace.dto.request.CustomerRequest;
import com.marketplace.minimarketplace.dto.response.CustomerResponse;
import com.marketplace.minimarketplace.entity.User;
import com.marketplace.minimarketplace.entity.UserProfile;
import com.marketplace.minimarketplace.exception.DuplicateResourceException;
import com.marketplace.minimarketplace.exception.ResourceNotFoundException;
import com.marketplace.minimarketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_validRequest_returnsCustomerResponse() {
        CustomerRequest request = new CustomerRequest("Alice", "alice@test.com", "pass123", "017", "Dhaka");
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertEquals(1L, response.getId());
        assertEquals("Alice", response.getName());
        assertEquals("alice@test.com", response.getEmail());
        assertEquals("017", response.getPhone());
        assertEquals("Dhaka", response.getAddress());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("ROLE_CUSTOMER", saved.getRole());
        assertEquals("encoded", saved.getPassword());
        assertEquals("017", saved.getProfile().getPhone());
    }

    @Test
    void createCustomer_duplicateEmail_throwsDuplicateResourceException() {
        CustomerRequest request = new CustomerRequest("Alice", "alice@test.com", "pass123", "017", "Dhaka");
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getCustomerById_existingCustomer_returnsResponse() {
        User user = User.builder().id(1L).name("Alice").email("alice@test.com").role("ROLE_CUSTOMER").build();
        user.setProfile(UserProfile.builder().phone("017").address("Dhaka").user(user).build());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CustomerResponse response = customerService.getCustomerById(1L);

        assertEquals(1L, response.getId());
        assertEquals("alice@test.com", response.getEmail());
        assertEquals("017", response.getPhone());
    }

    @Test
    void getAllCustomers_returnsAllResponses() {
        User user1 = User.builder().id(1L).name("Alice").email("alice@test.com").role("ROLE_CUSTOMER").build();
        User user2 = User.builder().id(2L).name("Bob").email("bob@test.com").role("ROLE_CUSTOMER").build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<CustomerResponse> responses = customerService.getAllCustomers();

        assertEquals(2, responses.size());
    }

    @Test
    void updateCustomer_existingCustomer_updatesAndReturnsResponse() {
        User user = User.builder().id(1L).name("Old").email("old@test.com").password("oldPass").role("ROLE_CUSTOMER").build();
        user.setProfile(UserProfile.builder().user(user).phone("old").address("old").build());

        CustomerRequest request = new CustomerRequest("New", "new@test.com", "newPass", "019", "Sylhet");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.updateCustomer(1L, request);

        assertEquals("New", response.getName());
        assertEquals("new@test.com", response.getEmail());
        assertEquals("019", response.getPhone());
        assertEquals("Sylhet", response.getAddress());
        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    void deleteCustomer_existingCustomer_deletesSuccessfully() {
        User user = User.builder().id(1L).name("Alice").email("alice@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));
        verify(userRepository).delete(user);
    }

    @Test
    void getCustomerById_missingCustomer_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(99L));
    }
}

