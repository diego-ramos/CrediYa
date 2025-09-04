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
    ),
    USER_EMAIL_FIND(
            "USR_ERR_003", "Error finding user by email", A_SYSTEM_FAILURE_OCCURRED
    ),
    USER_ID_FIND(
            "USR_ERR_004", "Error finding user by id", A_SYSTEM_FAILURE_OCCURRED
    ),
    ERROR_LOGIN_USER(
            "USR_ERR_005", "Error login user", A_SYSTEM_FAILURE_OCCURRED
    ),
    USER_ROLE_FIND(
            "USR_ERR_006", "Error finding user role", A_SYSTEM_FAILURE_OCCURRED
    ),
    PERMISSION_BY_SERVER_ID_FIND(
            "SOL_ERR_007", "Error finding permission by server id", A_SYSTEM_FAILURE_OCCURRED
    );

    private final String code;
    private final String description;
    private final String message;

    @Override
    public String toString(){
        return code + ": " + description + ": " + message;
    }
}

