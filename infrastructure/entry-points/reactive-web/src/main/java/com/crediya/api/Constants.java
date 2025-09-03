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
    public static final String LOGIN_REQUEST_RECEIVED = "Login request received for user: {}";
    public static final String LOGIN_SUCCESSFULLY= "Login successfully: {}";
    public static final String ERROR_LOGIN_USER = "Error while login user";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String MUST_PROVIDE_VALID_CREDENTIALS = "You must provide valid credentials";
    public static final String FORBIDDEN = "Forbidden";
    public static final String YOU_DONT_HAVE_PERMISSION_TO_ACCESS = "You do not have permission to access this resource";
    public static final String GRANTED_AUTHORITY = "Granted authority:";
}
