package com.retech.security;

import com.retech.entity.User;
import com.retech.exception.UnauthorizedAccessException;
import com.retech.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static Optional<String> getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.of(auth.getName());
    }

    public User getCurrentAuthenticatedUser() {
        String username = getCurrentUsername()
                .orElseThrow(() -> new UnauthorizedAccessException("You must be logged in to perform this operation"));
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Authenticated user record could not be found"));
    }

    public static boolean isAuthenticated() {
        return getCurrentUsername().isPresent();
    }
}
