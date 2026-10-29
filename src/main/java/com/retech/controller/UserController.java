package com.retech.controller;

import com.retech.dto.ApiResponse;
import com.retech.dto.UpdateProfileRequest;
import com.retech.dto.UserProfileDto;
import com.retech.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile() {
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileDto updated = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile details updated successfully", updated));
    }
}
