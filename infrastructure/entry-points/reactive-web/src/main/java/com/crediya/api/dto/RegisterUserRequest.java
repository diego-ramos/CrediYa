package com.crediya.api.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(
    @NotNull(message = "Id number is required")
    Integer idNumber,
    @NotBlank(message = "First names is required")
    String firstNames,
    @NotBlank(message = "Last names is required")
    String lastNames,
    @Email(message = "Email must be valid")
    String email,
    @NotNull(message = "Base Salary is required")
    Long baseSalary,
    String phone,
    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate birthDate,
    String address
){}
