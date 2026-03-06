package com.internship.stocks_api.errors;

import com.internship.stocks_api.shared.ApiError;

public final class AuthErrors {

    private AuthErrors() {}

    public static ApiError notFound(String email) {
        return ApiError.notFound(
                "Authentication.UserNotFound",
                "User with the email = '" + email + "' was not found"
        );
    }

    public static ApiError alreadyExists(String email) {
        return ApiError.conflict(
                "Authentication.UserAlreadyExists",
                "User with the email = '" + email + "' already exists"
        );
    }

    public static ApiError authProblem(String message) {
        return ApiError.conflict(
                "Authentication.UnexpectedError",
                "There was a problem with the authentication request: " + message
        );
    }
}
