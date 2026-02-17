package com.internship.stocks_api.shared;

public final class ApiError {

    private final String code;
    private final String message;
    private final int status;

    private ApiError(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public int status() {
        return status;
    }

    public static ApiError problem(String code, String message) {
        return new ApiError(code, message, 400);
    }

    public static ApiError notFound(String code, String message) {
        return new ApiError(code, message, 404);
    }

    public static ApiError conflict(String code, String message) {
        return new ApiError(code, message, 409);
    }

    public static ApiError forbidden(String code, String message) {
        return new ApiError(code, message, 403);
    }
}

