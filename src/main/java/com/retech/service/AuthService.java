package com.retech.service;

import com.retech.dto.LoginRequest;
import com.retech.dto.RegisterRequest;
import com.retech.dto.UserProfileDto;
import com.retech.entity.User;
import com.retech.entity.enums.Role;
import com.retech.exception.ValidationException;
import com.retech.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserProfileDto registerCustomer(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new ValidationException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ValidationException("Email address is already registered");
        }

        User user = new User(
                request.getUsername().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName().trim(),
                request.getPhone().trim(),
                request.getAddress() != null ? request.getAddress().trim() : null,
                request.getCity() != null ? request.getCity().trim() : null,
                request.getPostalCode() != null ? request.getPostalCode().trim() : null,
                Role.ROLE_CUSTOMER // Security rule: public registration form CANNOT assign ADMIN or other roles
        );

        User savedUser = userRepository.save(user);
        return UserProfileDto.fromEntity(savedUser);
    }

    public UserProfileDto login(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail().trim(), request.getPassword())
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail().trim(), request.getUsernameOrEmail().trim())
                .orElseThrow(() -> new ValidationException("Authenticated user could not be found"));

        return UserProfileDto.fromEntity(user);
    }
}
