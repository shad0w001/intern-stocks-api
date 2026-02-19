package com.internship.stocks_api.services;

import com.internship.stocks_api.clients.finnhub.FinnhubClient;
import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoUpdateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.errors.CompanyInfoErrors;
import com.internship.stocks_api.mappers.CompanyInfoMapper;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.repositories.CompanyInfoRepository;
import com.internship.stocks_api.repositories.CompanyStockInfoRepository;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyInfoServiceTests {

    @Mock
    private CompanyInfoRepository companyInfoRepository;

    @Mock
    private CompanyStockInfoRepository companyStockInfoRepository;

    @Mock
    private CompanyInfoMapper companyInfoMapper;

    @Mock
    private FinnhubClient finnhubClient;

    @InjectMocks
    private CompanyInfoService service;

    @BeforeEach
    void setUp() {
        companyInfoRepository = mock(CompanyInfoRepository.class);
        companyStockInfoRepository = mock(CompanyStockInfoRepository.class);
        finnhubClient = mock(FinnhubClient.class);
        companyInfoMapper = mock(CompanyInfoMapper.class);
        service = new CompanyInfoService(
                companyInfoRepository,
                companyStockInfoRepository,
                companyInfoMapper,
                finnhubClient
        );
    }

    @Test
    void getAllCompanyInfoEntries_ShouldReturnList_WhenCompaniesExist() {
        // arrange
        var company1 = new CompanyInfo();
        var company2 = new CompanyInfo();

        var dto1 = new CompanyInfoViewDto();
        var dto2 = new CompanyInfoViewDto();

        when(companyInfoRepository.findAll()).thenReturn(List.of(company1, company2));
        when(companyInfoMapper.toViewDto(company1)).thenReturn(dto1);
        when(companyInfoMapper.toViewDto(company2)).thenReturn(dto2);

        // act
        var result = service.getAllCompanyInfoEntries();

        // assert
        assertTrue(result.isSuccess());
        assertEquals(2, result.getValue().size());
        assertEquals(dto1, result.getValue().get(0));
        assertEquals(dto2, result.getValue().get(1));

        verify(companyInfoRepository).findAll();
        verify(companyInfoMapper).toViewDto(company1);
        verify(companyInfoMapper).toViewDto(company2);
    }

    @Test
    void getAllCompanyInfoEntries_ShouldReturnEmptyList_WhenNoCompaniesExist() {
        // arrange
        when(companyInfoRepository.findAll()).thenReturn(List.of());

        // act
        var result = service.getAllCompanyInfoEntries();

        // assert
        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isEmpty());

        verify(companyInfoRepository).findAll();
        verifyNoInteractions(companyInfoMapper);
    }

    @Test
    void getCompanyInfoEntry_ShouldReturnCompany_WhenCompanyExists() {
        // arrange
        var companyId = 1L;
        CompanyInfo company = new CompanyInfo();
        CompanyInfoViewDto dto = new CompanyInfoViewDto();

        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyInfoMapper.toViewDto(company)).thenReturn(dto);

        // act
        var result = service.getCompanyInfoEntry(companyId);

        // assert
        assertTrue(result.isSuccess());
        assertEquals(dto, result.getValue());

        verify(companyInfoRepository).findById(companyId);
        verify(companyInfoMapper).toViewDto(company);
    }

    @Test
    void getCompanyInfoEntry_ShouldReturnFailure_WhenCompanyDoesNotExist() {
        // arrange
        Long companyId = 999L;
        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.empty());

        // act
        var result = service.getCompanyInfoEntry(companyId);

        // assert
        assertTrue(result.isFailure());
        assertNotNull(result.getError());
        assertEquals("CompanyInfo.NotFound", result.getError().code());

        verify(companyInfoRepository).findById(companyId);
        verifyNoInteractions(companyInfoMapper);
    }

    @Test
    void createCompanyInfoEntry_Should_ReturnSuccess_WhenSymbolDoesNotExist() {
        // arrange
        var dto = new CompanyInfoCreateDto();
        dto.setSymbol("SOME");

        var entity = new CompanyInfo();
        var savedEntity = new CompanyInfo();
        var viewDto = new CompanyInfoViewDto();

        when(companyInfoRepository.findBySymbol(dto.getSymbol()))
                .thenReturn(Optional.empty());

        when(companyInfoMapper.createDtoToEntity(dto)).thenReturn(entity);

        when(companyInfoRepository.save(entity)).thenReturn(savedEntity);

        when(companyInfoMapper.toViewDto(savedEntity)).thenReturn(viewDto);

        // act
        Result<CompanyInfoViewDto> result = service.createCompanyInfoEntry(dto);

        // assert
        assertTrue(result.isSuccess());
        assertEquals(viewDto, result.getValue());
        verify(companyInfoRepository).save(entity);
    }

    @Test
    void createCompanyInfoEntry_Should_ReturnFailure_WhenSymbolAlreadyExists() {
        // arrange
        var dto = new CompanyInfoCreateDto();
        dto.setSymbol("SOME");

        when(companyInfoRepository.findBySymbol(dto.getSymbol()))
                .thenReturn(Optional.of(new CompanyInfo()));

        // act
        Result<CompanyInfoViewDto> result = service.createCompanyInfoEntry(dto);

        // assert
        assertTrue(result.isFailure());
        assertEquals(CompanyInfoErrors.symbolAlreadyExists("SOME").code(), result.getError().code());

        verify(companyInfoRepository, never()).save(any());
    }

    @Test
    void updateCompanyInfoEntry_Should_ReturnSuccess_WhenCompanyExists() {
        // arrange
        Long companyId = 1L;
        CompanyInfoUpdateDto dto = new CompanyInfoUpdateDto();

        CompanyInfo existingEntity = new CompanyInfo();
        CompanyInfo savedEntity = new CompanyInfo();
        CompanyInfoViewDto viewDto = new CompanyInfoViewDto();

        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.of(existingEntity));

        doNothing().when(companyInfoMapper).updateDtoToEntity(dto, existingEntity);

        when(companyInfoRepository.save(existingEntity)).thenReturn(savedEntity);

        when(companyInfoMapper.toViewDto(savedEntity)).thenReturn(viewDto);

        // act
        var result = service.updateCompanyInfoEntry(companyId, dto);

        // assert
        assertTrue(result.isSuccess());
        assertEquals(viewDto, result.getValue());
        verify(companyInfoMapper).updateDtoToEntity(dto, existingEntity);
        verify(companyInfoRepository).save(existingEntity);
    }

    @Test
    void updateCompanyInfoEntry_Should_ReturnFailure_WhenCompanyDoesNotExist() {
        // arrange
        Long companyId = 1L;
        CompanyInfoUpdateDto dto = new CompanyInfoUpdateDto();

        when(companyInfoRepository.findById(companyId)).thenReturn(Optional.empty());

        // act
        var result = service.updateCompanyInfoEntry(companyId, dto);

        // assert
        assertTrue(result.isFailure());
        assertEquals(CompanyInfoErrors.notFound(companyId).code(), result.getError().code());
        verify(companyInfoRepository, never()).save(any());
    }
}
