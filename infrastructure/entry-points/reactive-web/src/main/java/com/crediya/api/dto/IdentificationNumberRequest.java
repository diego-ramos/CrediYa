package com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//@Data
//@AtLeastOneRequired(first = "identificationNumber", second = "email")
public record IdentificationNumberRequest (
    @Schema(description = "User Identification Number", example = "79948037")
    @NotNull(message = "identificationNumber is required")
    @Positive(message = "identificationNumber must be greater than 0")
    Integer identificationNumber

//    @Schema(description = "User email", example = "darp@test.com")
//    @Email(message = "Email must be valid")
//    String email;
){}
