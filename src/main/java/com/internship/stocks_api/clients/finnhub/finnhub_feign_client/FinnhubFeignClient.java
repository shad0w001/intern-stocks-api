package com.internship.stocks_api.clients.finnhub.finnhub_feign_client;

import com.internship.stocks_api.models.CompanyStockInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "finnhubFeignClient",
        url = "${finnhub.api.base-url}"
)
public interface FinnhubFeignClient {
    @GetMapping("/stock/profile2")
    CompanyStockInfo getCompanyProfile(@RequestParam("symbol") String symbol, @RequestParam("token") String token);

    @GetMapping("/stock/peers")
    List<String> getCompanyPeers(@RequestParam("symbol") String symbol, @RequestParam("token") String token);
}
