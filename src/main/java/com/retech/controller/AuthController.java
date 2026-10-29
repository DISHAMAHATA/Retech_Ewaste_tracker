package com.retech.controller;

import com.retech.dto.ApiResponse;
import com.retech.dto.LoginRequest;
import com.retech.dto.RegisterRequest;
import com.retech.dto.UserProfileDto;
import com.retech.security.SecurityUtils;
import com.retech.service.AuthService;
import com.retech.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserProfileDto created = authService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful! You may now log in.", created));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserProfileDto>> login(@Valid @RequestBody LoginRequest request,
                                                             HttpServletRequest httpRequest) {
        UserProfileDto profile = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success("Sign in successful", profile));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentSession() {
        if (!SecurityUtils.isAuthenticated()) {
            return ResponseEntity.ok(ApiResponse.error("No active session"));
        }
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success("Active session retrieved", profile));
    }
}
