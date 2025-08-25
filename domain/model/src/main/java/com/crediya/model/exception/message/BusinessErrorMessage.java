package com.crediya.model.exception.message;

import static com.crediya.model.exception.Constants.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorMessage {
    SALARY_OUT_OF_RANGE(
            "BUSS_ERR_001", "Salary out of range", A_SYSTEM_FAILURE_OCCURRED
    );

    private final String code;
    private final String description;
    private final String message;
}
