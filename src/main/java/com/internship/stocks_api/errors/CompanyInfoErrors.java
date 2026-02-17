package com.internship.stocks_api.errors;

import com.internship.stocks_api.shared.ApiError;

public final class CompanyInfoErrors {

    private CompanyInfoErrors() {}

    public static ApiError notFound(Long companyId) {
        return ApiError.notFound(
                "CompanyInfo.NotFound",
                "Company with id = '" + companyId + "' was not found"
        );
    }

    public static ApiError symbolAlreadyExists(String symbol) {
        return ApiError.conflict(
                "CompanyInfo.SymbolAlreadyExists",
                "Company with symbol '" + symbol + "' already exists"
        );
    }
}

