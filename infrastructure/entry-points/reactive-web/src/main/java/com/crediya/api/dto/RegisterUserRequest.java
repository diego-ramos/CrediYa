package com.crediya.api.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(

    @Schema(description = "User Identification Number", example = "79948037")
    @NotNull(message = "Id number is required")
    Integer idNumber,

    @Schema(description = "User first names", example = "Diego Alberto")
    @NotBlank(message = "Firsts names are required")
    String firstNames,

    @Schema(description = "User last names", example = "Ramos Patarroyo")
    @NotBlank(message = "Lasts names are required")
    String lastNames,

    @Schema(description = "User email", example = "darp@test.com")
    @Email(message = "Email must be valid")
    String email,

    @Schema(description = "User Base Salary", example = "1000000")
    @NotNull(message = "Base Salary is required")
    Long baseSalary,

    @Schema(description = "User Base Salary", example = "3113562536")
    String phone,

    @Schema(description = "User birth Date", pattern = "yyyy-MM-dd", example = "1978-06-22")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate birthDate,

    @Schema(description = "User Address", example = "Calle 3 26 #33-45")
    String address
){}
