package com.internship.stocks_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoUpdateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.errors.CompanyInfoErrors;
import com.internship.stocks_api.services.CompanyInfoService;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyInfoController.class)
class CompanyInfoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

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

    @Test
    void getCompanyInfoEntry_Should_ReturnOk_WhenCompanyExists() throws Exception {
        // arrange
        var company = new CompanyInfoViewDto();
        company.setId(1L);
        company.setName("ExampleCo");

        when(service.getCompanyInfoEntry(1L))
                .thenReturn(Result.success(company));

        String expectedJson = objectMapper.writeValueAsString(company);

        // act + assert
        mockMvc.perform(get("/companies/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void getCompanyInfoEntry_Should_ReturnNotFound_WhenCompanyDoesNotExist() throws Exception {
        // arrange
        var id = 999L;
        when(service.getCompanyInfoEntry(id))
                .thenReturn(Result.failure(CompanyInfoErrors.notFound(id)));

        // act + assert
        mockMvc.perform(get("/companies/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Company with id = '" + id + "' was not found"));
    }

    @Test
    void createCompanyEntry_Should_ReturnCreated_WhenValid() throws Exception {
        // arrange
        var dto = new CompanyInfoCreateDto();
        dto.setName("ExampleCo");
        dto.setCountry("US");
        dto.setSymbol("EXMPL");
        dto.setWebsite("https://example.com");
        dto.setEmail("a@example.com");

        var createdCompany = new CompanyInfoViewDto();
        createdCompany.setId(1L);
        createdCompany.setName("ExampleCo");
        createdCompany.setSymbol("EXMPL");

        when(service.createCompanyInfoEntry(any(CompanyInfoCreateDto.class)))
                .thenReturn(Result.success(createdCompany));

        String requestJson = objectMapper.writeValueAsString(dto);
        String responseJson = objectMapper.writeValueAsString(createdCompany);

        // act + assert
        mockMvc.perform(post("/companies")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/companies/1"))
                .andExpect(content().json(responseJson));
    }

    @Test
    void createCompanyEntry_Should_ReturnBadRequest_WhenInvalidCountry() throws Exception {
        // arrange
        var dto = new CompanyInfoCreateDto();
        dto.setName("Some Company");
        dto.setCountry("A"); // less than 2 symbols
        dto.setSymbol("SOME");

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(post("/companies")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCompanyEntry_Should_ReturnError_WhenServiceFails() throws Exception {
        // arrange
        var dto = new CompanyInfoCreateDto();
        dto.setName("Some Company");
        dto.setCountry("US");
        dto.setSymbol("SOME");
        dto.setWebsite("https://example.com");
        dto.setEmail("a@example.com");

        when(service.createCompanyInfoEntry(any(CompanyInfoCreateDto.class)))
                .thenReturn(Result.failure(CompanyInfoErrors.symbolAlreadyExists(dto.getSymbol())));

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(post("/companies")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(content().string("Company with symbol '" + dto.getSymbol() + "' already exists"));
    }

    @Test
    void updateCompany_Should_ReturnNoContent_WhenValid() throws Exception {
        // arrange
        var dto = new CompanyInfoUpdateDto();
        dto.setName("Some");
        dto.setCountry("US");
        dto.setSymbol("SOME");
        dto.setWebsite("https://example.com");
        dto.setEmail("a@example.com");

        when(service.updateCompanyInfoEntry(eq(1L), any(CompanyInfoUpdateDto.class)))
                .thenReturn(Result.success(null));

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(put("/companies/1")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateCompany_Should_ReturnBadRequest_WhenInvalid() throws Exception {
        // arrange
        var dto = new CompanyInfoUpdateDto();
        dto.setName("Some");
        dto.setCountry("A"); // invalid input
        dto.setSymbol("SOME");
        dto.setWebsite("https://example.com");
        dto.setEmail("a@example.com");

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(put("/companies/1")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("country"))
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }

    @Test
    void updateCompany_Should_ReturnNotFound_WhenCompanyDoesNotExist() throws Exception {
        // arrange
        var id = 999L;
        var dto = new CompanyInfoUpdateDto();
        dto.setName("Some");
        dto.setCountry("US");
        dto.setSymbol("SOME");
        dto.setWebsite("https://example.com");
        dto.setEmail("a@example.com");

        when(service.updateCompanyInfoEntry(eq(id), any(CompanyInfoUpdateDto.class)))
                .thenReturn(Result.failure(CompanyInfoErrors.notFound(id)));

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(put("/companies/999")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Company with id = '" + id + "' was not found"));
    }

}