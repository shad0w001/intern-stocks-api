package com.internship.stocks_api.errors;

import com.internship.stocks_api.shared.ApiError;

public final class FinnhubApiErrors {
    private FinnhubApiErrors() {}

    public static ApiError notFound(String symbol){
        return ApiError.notFound(
                "FinnhubApiError.NotFound",
                "Company with the symbol = '" + symbol + "' was not found"
        );
    }
}
