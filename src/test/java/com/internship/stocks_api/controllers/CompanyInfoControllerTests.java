package com.internship.stocks_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.services.CompanyInfoService;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.web.SpringBootMockServletContext;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyInfoController.class)
class CompanyInfoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CompanyInfoService service;

    @Test
    void getAllCompanyInfoEntries_Should_ReturnOk() throws Exception{
        //arrange
        var company = new CompanyInfoViewDto();
        company.setId(1L);
        company.setName("example");

        when(service.getAllCompanyInfoEntries())
                .thenReturn(Result.success(List.of(company)));

        String expectedJson = objectMapper.writeValueAsString(List.of(company));

        //assert + act
        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}