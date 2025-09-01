package com.crediya.model.exception.message;

import static com.crediya.model.exception.Constants.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorMessage {
    SALARY_OUT_OF_RANGE(
            "BUSS_ERR_001", "Salary out of range", VERIFY_YOUR_DATA
    ),
    INVALID_EMAIL(
            "BUSS_ERR_002", "Email is invalid", VERIFY_YOUR_DATA
    ),
    EMAIL_ALREADY_REGISTERED(
            "BUSS_ERR_003", "Email already registered", VERIFY_YOUR_DATA
    ),
    IDENTIFICATION_NUMBER_ALREADY_REGISTERED(
            "BUSS_ERR_004", "Identification number already registered", VERIFY_YOUR_DATA
    ),
    ERROR_LOGIN_USER(
            "BUSS_ERR_005", "Error login user", VERIFY_YOUR_DATA
    );

    private final String code;
    private final String description;
    private final String message;

    @Override
    public String toString(){
        return code + ": " + description + ": " + message;
    }
}
