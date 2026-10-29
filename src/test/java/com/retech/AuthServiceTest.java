package com.retech;

import com.retech.dto.RegisterRequest;
import com.retech.dto.UserProfileDto;
import com.retech.entity.enums.Role;
import com.retech.exception.ValidationException;
import com.retech.repository.UserRepository;
import com.retech.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register customer with hashed password and CUSTOMER role")
    void testRegisterCustomerSuccess() {
        RegisterRequest req = new RegisterRequest(
                "rahul_tech",
                "rahul@example.com",
                "SecurePassword123!",
                "Rahul Verma",
                "+91-9876543210",
                "123 Tech Park Road",
                "Metro City",
                "560001"
        );

        UserProfileDto result = authService.registerCustomer(req);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("rahul_tech", result.getUsername());
        assertEquals("rahul@example.com", result.getEmail());
        assertEquals(Role.ROLE_CUSTOMER, result.getRole());
        assertTrue(result.isActive());

        // Verify password hashing in DB
        var userInDb = userRepository.findById(result.getId()).orElseThrow();
        assertNotEquals("SecurePassword123!", userInDb.getPasswordHash());
        assertTrue(passwordEncoder.matches("SecurePassword123!", userInDb.getPasswordHash()));
    }

    @Test
    @DisplayName("Should reject registration when username is already taken")
    void testDuplicateUsernameRejected() {
        RegisterRequest req1 = new RegisterRequest("same_user", "email1@example.com", "Password123!", "User One", "9876543210", null, null, null);
        authService.registerCustomer(req1);

        RegisterRequest req2 = new RegisterRequest("same_user", "email2@example.com", "Password123!", "User Two", "9876543211", null, null, null);
        ValidationException ex = assertThrows(ValidationException.class, () -> authService.registerCustomer(req2));
        assertTrue(ex.getMessage().contains("Username is already taken"));
    }

    @Test
    @DisplayName("Should reject registration when email is already registered")
    void testDuplicateEmailRejected() {
        RegisterRequest req1 = new RegisterRequest("user_one", "shared@example.com", "Password123!", "User One", "9876543210", null, null, null);
        authService.registerCustomer(req1);

        RegisterRequest req2 = new RegisterRequest("user_two", "shared@example.com", "Password123!", "User Two", "9876543211", null, null, null);
        ValidationException ex = assertThrows(ValidationException.class, () -> authService.registerCustomer(req2));
        assertTrue(ex.getMessage().contains("Email address is already registered"));
    }
}
