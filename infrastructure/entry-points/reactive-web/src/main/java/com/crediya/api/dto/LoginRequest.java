package com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
    @Schema(description = "User email", example = "darp@test.com")
    @Email(message = "Email must be valid")
    @NotBlank(message = "User email is required")
    String email,

    @Schema(description = "password", example = "abc123")
    @NotBlank(message = "User password is required")
    String password
){}
