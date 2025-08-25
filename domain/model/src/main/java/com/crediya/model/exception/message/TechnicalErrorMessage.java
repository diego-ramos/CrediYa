package com.crediya.model.exception.message;

import static com.crediya.model.exception.Constants.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalErrorMessage {

    USER_SAVE(
            "USR_ERR_001", "Error registering user", A_SYSTEM_FAILURE_OCCURRED
    ),
    USER_EMPTY(
            "USR_ERR_002", "Repository returned empty", A_SYSTEM_FAILURE_OCCURRED
    );

    private final String code;
    private final String description;
    private final String message;

    @Override
    public String toString(){
        return code + ": " + description + ": " + message;
    }
}

