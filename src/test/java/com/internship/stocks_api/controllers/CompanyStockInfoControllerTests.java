package com.internship.stocks_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.stocks_api.dtos.company_stock_info.CompanyStockInfoViewDto;
import com.internship.stocks_api.errors.CompanyInfoErrors;
import com.internship.stocks_api.errors.FinnhubApiErrors;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.services.CompanyInfoService;
import com.internship.stocks_api.services.CompanyStockInfoService;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompanyStockInfoController.class)
class CompanyStockInfoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private CompanyStockInfoService service;

    @Test
    void getCompanyStockInfo_Should_ReturnOk_WhenCompanyExists() throws Exception {
        // arrange
        var stockDto = new CompanyStockInfoViewDto(
                1L,
                "Some Company",
                "US",
                "SOME",
                "https://example.com",
                "a@example.com",
                null,
                1_000_000_000D,
                1_000_000D
        );

        when(service.getCompanyStockInfo(1L))
                .thenReturn(Result.success(stockDto));

        String expectedJson = objectMapper.writeValueAsString(stockDto);

        // act + assert
        mockMvc.perform(get("/companies/company-stocks/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getCompanyStockInfo_Should_ReturnNotFound_WhenCompanyDoesNotExist() throws Exception {
        // arrange
        var id = 999L;
        when(service.getCompanyStockInfo(id))
                .thenReturn(Result.failure(CompanyInfoErrors.notFound(id)));

        // act + assert
        mockMvc.perform(get("/companies/company-stocks/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Company with id = '" + id + "' was not found"));
    }

    @Test
    void getCompanyStockInfo_Should_ReturnNotFound_WhenFinnhubDoesNotFindSymbol() throws Exception {
        // arrange
        var companyInfo = new CompanyInfo(
                1L,
                "Some Company",
                "US",
                "SOME",
                "https://example.com",
                "a@example.com",
                LocalDateTime.now()
        );

        when(service.getCompanyStockInfo(companyInfo.getId()))
                .thenReturn(Result.failure(FinnhubApiErrors.notFound(companyInfo.getSymbol())));

        // act + assert
        mockMvc.perform(get("/companies/company-stocks/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Company with the symbol = '" + companyInfo.getSymbol() + "' was not found"));
    }
}
