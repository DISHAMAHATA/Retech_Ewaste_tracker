package com.retech.service;

import com.retech.dto.AdminUserDto;
import com.retech.dto.UpdateProfileRequest;
import com.retech.dto.UpdateUserRoleDto;
import com.retech.dto.UserProfileDto;
import com.retech.entity.User;
import com.retech.entity.enums.Role;
import com.retech.exception.ResourceNotFoundException;
import com.retech.exception.ValidationException;
import com.retech.repository.UserRepository;
import com.retech.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public UserService(UserRepository userRepository, SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    public UserProfileDto getCurrentUserProfile() {
        User user = securityUtils.getCurrentAuthenticatedUser();
        return UserProfileDto.fromEntity(user);
    }

    @Transactional
    public UserProfileDto updateProfile(UpdateProfileRequest request) {
        User user = securityUtils.getCurrentAuthenticatedUser();
        user.setFullName(request.getFullName().trim());
        user.setPhone(request.getPhone().trim());
        user.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
        user.setCity(request.getCity() != null ? request.getCity().trim() : null);
        user.setPostalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null);

        User saved = userRepository.save(user);
        return UserProfileDto.fromEntity(saved);
    }

    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AdminUserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AdminUserDto> getActiveAgents() {
        return userRepository.findByRoleAndActiveTrue(Role.ROLE_AGENT).stream()
                .map(AdminUserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AdminUserDto> getActiveOperators() {
        return userRepository.findByRoleAndActiveTrue(Role.ROLE_OPERATOR).stream()
                .map(AdminUserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminUserDto updateUserRoleAndStatus(Long userId, UpdateUserRoleDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        User currentUser = securityUtils.getCurrentAuthenticatedUser();
        if (user.getId().equals(currentUser.getId()) && dto.getActive() != null && !dto.getActive()) {
            throw new ValidationException("Administrators cannot deactivate their own account");
        }

        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        User updated = userRepository.save(user);
        return AdminUserDto.fromEntity(updated);
    }
}
