package com.crediya.model.user;

import lombok.Data;

@Data
public class AuthResponse {
    private final String token;
    private final String username;
    private final String roleName;
}

