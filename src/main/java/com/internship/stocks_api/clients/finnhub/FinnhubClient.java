package com.internship.stocks_api.clients.finnhub;

import com.internship.stocks_api.models.CompanyStockInfo;

public interface FinnhubClient {

    CompanyStockInfo getCompanyProfile(String symbol);
}
