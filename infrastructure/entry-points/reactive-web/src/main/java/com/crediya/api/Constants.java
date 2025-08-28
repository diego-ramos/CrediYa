package com.crediya.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String USER_REGISTRATION_REQUEST_RECEIVED = "Received registerUser request with user: {}";
    public static final String INVALID_REQUEST = "Invalid request.";
    public static final String USER_REGISTER_SUCCESS = "User registered successfully: {}";
    public static final String ERROR_REGISTERING_USER = "Error while registering user";
    public static final String USER_SEARCH_REQUEST_RECEIVED = "Received user search request with user identification number: {}";
    public static final String ERROR_SEARCHING_USER = "Error while searching user";
    public static final String USER_SEARCH_SUCCESS = "User searching successfully: {}";
    public static final String IDENTIFICATION_NUMBER_MUST_BE_INTEGER = "User identification number must be an integer";
    public static final String USER_NOT_FOUND = "User not found: ";
}
