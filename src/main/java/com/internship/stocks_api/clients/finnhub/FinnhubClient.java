package com.internship.stocks_api.clients.finnhub;

import com.internship.stocks_api.models.CompanyStockInfo;

import java.util.List;

public interface FinnhubClient {

    CompanyStockInfo getCompanyProfile(String symbol);
    List<String> getCompanyPeers(String symbol);
}
