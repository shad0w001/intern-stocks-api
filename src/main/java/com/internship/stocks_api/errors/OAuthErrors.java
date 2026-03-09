package com.internship.stocks_api.errors;

import com.internship.stocks_api.shared.ApiError;

public final class OAuthErrors {

    private OAuthErrors() {}

    public static ApiError userNotFound(String email) {
        return ApiError.notFound(
                "OAuth.UserNotFound",
                "No user found with email = '" + email + "'"
        );
    }

    public static ApiError oauthAccountAlreadyExists(String provider, String providerUserId) {
        return ApiError.conflict(
                "OAuth.AccountAlreadyExists",
                "OAuth account for provider = '" + provider + "', userId = '" + providerUserId + "' already exists"
        );
    }

    public static ApiError authProblem(String message) {
        return ApiError.conflict(
                "OAuth.UnexpectedError",
                "There was a problem with the OAuth authentication: " + message
        );
    }
}