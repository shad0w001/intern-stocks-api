package com.internship.stocks_api.shared;

public final class Result<T> {

    private final T value;
    private final ApiError error;

    private Result(T value, ApiError error) {
        this.value = value;
        this.error = error;
    }

    public boolean isSuccess() {
        return error == null;
    }

    public boolean isFailure() {
        return error != null;
    }

    public T getValue() {
        if (isFailure()) {
            throw new IllegalStateException("Cannot access value of a failure result");
        }
        return value;
    }

    public ApiError getError() {
        if (isSuccess()) {
            throw new IllegalStateException("Cannot access error of a success result");
        }
        return error;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(ApiError error) {
        return new Result<>(null, error);
    }
}

