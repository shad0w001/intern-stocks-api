package com.internship.stocks_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.stocks_api.dtos.company_peers.CompanyPeerViewDto;
import com.internship.stocks_api.errors.CompanyInfoErrors;
import com.internship.stocks_api.errors.FinnhubApiErrors;
import com.internship.stocks_api.security.JwtAuthTokenFilter;
import com.internship.stocks_api.services.CompanyPeerService;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CompanyPeerController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthTokenFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "finnhub.client.type=rest",
        "finnhub.api.base-url=http://localhost",
        "finnhub.api.key=dummy"
})
class CompanyPeerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private CompanyPeerService service;

    @Test
    void getCompanyPeers_Should_ReturnOk_WhenCompanyExists() throws Exception {
        // arrange
        String symbol = "AAPL";
        var peersDto = new CompanyPeerViewDto(
                symbol,
                List.of("EMC", "HPQ", "DELL")
        );

        when(service.getPeersBySymbol(symbol))
                .thenReturn(Result.success(peersDto));

        String expectedJson = objectMapper.writeValueAsString(peersDto);

        // act + assert
        mockMvc.perform(get("/companies/{symbol}/peers", symbol))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getCompanyPeers_Should_ReturnNotFound_WhenCompanyDoesNotExist() throws Exception {
        // arrange
        String symbol = "UNKNOWN";

        when(service.getPeersBySymbol(symbol))
                .thenReturn(Result.failure(CompanyInfoErrors.symbolNotFound(symbol)));

        // act + assert
        mockMvc.perform(get("/companies/{symbol}/peers", symbol))
                .andExpect(status().isNotFound())
                .andExpect(content().string(CompanyInfoErrors.symbolNotFound(symbol).message()));
    }

    @Test
    void getCompanyPeers_Should_ReturnFailure_WhenFinnhubApiFails() throws Exception {
        // arrange
        String symbol = "AAPL";

        when(service.getPeersBySymbol(symbol))
                .thenReturn(Result.failure(FinnhubApiErrors.notFound(symbol)));

        // act + assert
        mockMvc.perform(get("/companies/{symbol}/peers", symbol))
                .andExpect(status().isNotFound())
                .andExpect(content().string(FinnhubApiErrors.notFound(symbol).message()));
    }
}
