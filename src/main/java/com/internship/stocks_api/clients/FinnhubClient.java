package com.internship.stocks_api.clients;

import com.internship.stocks_api.models.CompanyStockInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class FinnhubClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public FinnhubClient(
            @Value("${finnhub.api.base-url}") String baseUrl,
            @Value("${finnhub.api.key}") String apiKey
    ) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public CompanyStockInfo getCompanyProfile(String symbol) {
        String url = baseUrl + "/stock/profile2?symbol={symbol}&token={token}";

        try {
            return restTemplate.getForObject(url, CompanyStockInfo.class, symbol, apiKey);
        } catch (RestClientException ex) {
            throw new RestClientException("Failed to fetch Finnhub profile for symbol " + symbol, ex);
        }
    }
}
