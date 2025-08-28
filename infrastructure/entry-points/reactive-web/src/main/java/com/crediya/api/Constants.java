package com.crediya.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String USER_REGISTRATION_REQUEST_RECEIVED = "Received registerUser request with user: {}";
    public static final String INVALID_REQUEST = "Invalid request.";
    public static final String USER_REGISTER_SUCCESS = "User registered successfully: {}";
    public static final String ERROR_REGISTERING_USER = "Error while registering user";
}
