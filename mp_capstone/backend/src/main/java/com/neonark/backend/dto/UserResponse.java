package com.neonark.backend.dto;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
}
