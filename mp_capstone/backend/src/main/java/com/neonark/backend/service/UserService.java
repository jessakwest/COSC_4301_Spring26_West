package com.neonark.backend.service;

import com.neonark.backend.dto.UserResponse;
import com.neonark.backend.entity.User;
import com.neonark.backend.exception.ForbiddenException;
import com.neonark.backend.exception.UnauthorizedException;
import com.neonark.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    //simulated authorization check
    public void validateAdminAccess(String role) {
        if (role == null || role.isBlank()) {
            throw new UnauthorizedException("Authentication required.");
        }
        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new ForbiddenException("Access denied.");
        }
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build()).toList();
    }
}
