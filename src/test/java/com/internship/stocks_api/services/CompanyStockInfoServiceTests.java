package com.internship.stocks_api.services;

import com.internship.stocks_api.clients.FinnhubClient;
import com.internship.stocks_api.dtos.company_stock_info.CompanyStockInfoViewDto;
import com.internship.stocks_api.errors.FinnhubApiErrors;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.models.CompanyStockInfo;
import com.internship.stocks_api.repositories.CompanyInfoRepository;
import com.internship.stocks_api.repositories.CompanyStockInfoRepository;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

class CompanyStockInfoServiceTests {

    @Mock
    private CompanyInfoRepository companyInfoRepository;

    @Mock
    private CompanyStockInfoRepository companyStockInfoRepository;

    @Mock
    private FinnhubClient finnhubClient;

    @InjectMocks
    private CompanyStockInfoService service;

    @BeforeEach
    void setUp() {
        companyInfoRepository = mock(CompanyInfoRepository.class);
        companyStockInfoRepository = mock(CompanyStockInfoRepository.class);
        finnhubClient = mock(FinnhubClient.class);
        service = new CompanyStockInfoService(
                companyInfoRepository,
                companyStockInfoRepository,
                finnhubClient
        );
    }

    @Test
    void getCompanyStockInfo_Should_ReturnFailure_WhenCompanyDoesNotExist() {
        // arrange
        Long companyId = 1L;
        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.empty());

        // act
        Result<CompanyStockInfoViewDto> result = service.getCompanyStockInfo(companyId);

        // assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().code()).isEqualTo("CompanyInfo.NotFound");
        assertThat(result.getError().message()).isEqualTo("Company with id = '1' was not found");

        verifyNoInteractions(companyStockInfoRepository, finnhubClient);
    }

    @Test
    void getCompanyStockInfo_Should_ReturnSuccess_WhenStockInfoExistsInRepo() {
        // arrange
        var companyId = 1L;
        var company = new CompanyInfo();
        company.setId(companyId);
        company.setName("Some");
        company.setSymbol("SOME");

        var stockInfo = new CompanyStockInfo();
        stockInfo.setSymbol("SOME");
        stockInfo.setDate(LocalDate.now());
        stockInfo.setMarketCapitalization(1000D);
        stockInfo.setShareOutstanding(500D);

        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyStockInfoRepository.findBySymbolAndDate("SOME", LocalDate.now()))
                .thenReturn(Optional.of(stockInfo));

        // act
        var result = service.getCompanyStockInfo(companyId);

        // assert
        assertTrue(result.isSuccess());
        assertEquals("Some", result.getValue().getName());
        assertEquals(1000D, result.getValue().getMarketCapitalization());
        assertEquals(500D, result.getValue().getShareOutstanding());

        verifyNoInteractions(finnhubClient);
    }

    @Test
    void getCompanyStockInfo_Should_CallFinnhubAndReturnSuccess_WhenStockInfoNotInRepo() {
        // arrange
        var companyId = 1L;
        var company = new CompanyInfo();
        company.setId(companyId);
        company.setName("Some");
        company.setSymbol("SOME");
        company.setCountry("US");
        company.setWebsite("https://example.com");
        company.setEmail("a@example.com");
        company.setCreatedAt(null);

        var stockInfoFromApi = new CompanyStockInfo();
        stockInfoFromApi.setSymbol("SOME");
        stockInfoFromApi.setDate(LocalDate.now());
        stockInfoFromApi.setMarketCapitalization(2000D);
        stockInfoFromApi.setShareOutstanding(1000D);

        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.of(company));

        when(companyStockInfoRepository.findBySymbolAndDate("SOME", LocalDate.now()))
                .thenReturn(Optional.empty());

        when(finnhubClient.getCompanyProfile("SOME")).thenReturn(stockInfoFromApi);

        when(companyStockInfoRepository.save(stockInfoFromApi)).thenReturn(stockInfoFromApi);

        // act
        var result = service.getCompanyStockInfo(companyId);

        // assert
        assertTrue(result.isSuccess());
        assertEquals("Some", result.getValue().getName());
        assertEquals(2000L, result.getValue().getMarketCapitalization());
        assertEquals(1000L, result.getValue().getShareOutstanding());

        verify(finnhubClient, times(1)).getCompanyProfile("SOME");
        verify(companyStockInfoRepository, times(1)).save(stockInfoFromApi);
    }

    @Test
    void getCompanyStockInfo_Should_ReturnFailure_WhenFinnhubClientFails() {
        // arrange
        var companyId = 1L;
        var company = new CompanyInfo();
        company.setId(companyId);
        company.setName("Some");
        company.setSymbol("SOME");

        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.of(company));

        when(companyStockInfoRepository.findBySymbolAndDate("SOME", LocalDate.now()))
                .thenReturn(Optional.empty());

        when(finnhubClient.getCompanyProfile("SOME"))
                .thenThrow(new RestClientException("API down"));

        // act
        var result = service.getCompanyStockInfo(companyId);

        // assert
        assertTrue(result.isFailure());
        assertEquals(FinnhubApiErrors.notFound("SOME").code(), result.getError().code());
        assertEquals(FinnhubApiErrors.notFound("SOME").message(), result.getError().message());

        verify(companyStockInfoRepository, never()).save(any());
    }
}
