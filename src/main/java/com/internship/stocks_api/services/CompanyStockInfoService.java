package com.internship.stocks_api.services;

import com.internship.stocks_api.clients.FinnhubClient;
import com.internship.stocks_api.dtos.company_stock_info.CompanyStockInfoViewDto;
import com.internship.stocks_api.errors.CompanyInfoErrors;
import com.internship.stocks_api.errors.FinnhubApiErrors;
import com.internship.stocks_api.models.CompanyStockInfo;
import com.internship.stocks_api.repositories.CompanyInfoRepository;
import com.internship.stocks_api.repositories.CompanyStockInfoRepository;
import com.internship.stocks_api.shared.Result;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyStockInfoService {

    private final CompanyInfoRepository companyInfoRepository;
    private final CompanyStockInfoRepository companyStockInfoRepository;
    private final FinnhubClient finnhubClient;

    public Result<CompanyStockInfoViewDto> getCompanyStockInfo(Long id) {
        var company = companyInfoRepository.findById(id).orElse(null);
        if (company == null) {
            return Result.failure(CompanyInfoErrors.notFound(id));
        }

        CompanyStockInfo response;

        var stockInfo = companyStockInfoRepository.findBySymbolAndDate(company.getSymbol(), LocalDate.now());
        if(stockInfo.isPresent()){
            response = stockInfo.get();
        }
        else{
            try {
                response = finnhubClient.getCompanyProfile(company.getSymbol());
                response.setSymbol(company.getSymbol());
                response.setDate(LocalDate.now());
                companyStockInfoRepository.save(response);
            } catch (RestClientException ex) {
                return Result.failure(FinnhubApiErrors.notFound(company.getSymbol()));
            }
        }

        CompanyStockInfoViewDto dto = new CompanyStockInfoViewDto(
                company.getId(),
                company.getName(),
                company.getCountry(),
                company.getSymbol(),
                company.getWebsite(),
                company.getEmail(),
                company.getCreatedAt(),
                response.getMarketCapitalization(),
                response.getShareOutstanding()
        );

        return Result.success(dto);
    }
}
