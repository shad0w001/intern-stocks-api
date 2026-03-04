package com.internship.stocks_api.clients.finnhub;

import com.internship.stocks_api.models.CompanyStockInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@ConditionalOnProperty(
        name = "finnhub.client.type",
        havingValue = "rest",
        matchIfMissing = true // default client in case application.properties breaks for whatever reason
)
public class FinnhubRestTemplateClientImpl implements FinnhubClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final String apiKey;

    public FinnhubRestTemplateClientImpl(
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

    @Override
    public List<String> getCompanyPeers(String symbol) {
        String url = baseUrl + "/stock/peers?symbol={symbol}&token={token}";
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {},
                symbol,
                apiKey
        );
        return response.getBody();
    }
}

