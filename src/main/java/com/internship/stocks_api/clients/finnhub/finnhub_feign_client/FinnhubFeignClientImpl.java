package com.internship.stocks_api.clients.finnhub.finnhub_feign_client;

import com.internship.stocks_api.clients.finnhub.FinnhubClient;
import com.internship.stocks_api.models.CompanyStockInfo;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "finnhub.client.type",
        havingValue = "feign"
)
public class FinnhubFeignClientImpl implements FinnhubClient {
    private final FinnhubFeignClient api;
    private final String apiKey;

    public FinnhubFeignClientImpl(FinnhubFeignClient api, @Value("${finnhub.api.key}") String apiKey) {
        this.api = api;
        this.apiKey = apiKey;
    }

    @Override
    public CompanyStockInfo getCompanyProfile(String symbol) {
        return api.getCompanyProfile(symbol, apiKey);
    }
}
