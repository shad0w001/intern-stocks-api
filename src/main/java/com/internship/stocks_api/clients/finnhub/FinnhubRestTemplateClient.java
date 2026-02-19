package com.internship.stocks_api.clients.finnhub;

import com.internship.stocks_api.models.CompanyStockInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(
        name = "finnhub.client.type",
        havingValue = "rest",
        matchIfMissing = true // default client in case application.properties breaks for whatever reason
)
public class FinnhubRestTemplateClient implements FinnhubClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String apiKey;

    public FinnhubRestTemplateClient(
            @Value("${finnhub.api.base-url}") String baseUrl,
            @Value("${finnhub.api.key}") String apiKey
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Override
    public CompanyStockInfo getCompanyProfile(String symbol) {
        String url = baseUrl + "/stock/profile2?symbol={symbol}&token={token}";
        return restTemplate.getForObject(url, CompanyStockInfo.class, symbol, apiKey);
    }
}

