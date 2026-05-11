package com.neonark.backend.controller;


import com.neonark.backend.dto.UserResponse;
import com.neonark.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    //route 8: GET /api/admin/users -- lists all users
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(required = false) String role) {

        userService.validateAdminAccess(role);
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
