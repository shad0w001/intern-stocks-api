package com.internship.stocks_api.services;

import com.internship.stocks_api.clients.finnhub.FinnhubClient;
import com.internship.stocks_api.dtos.company_peers.CompanyPeerViewDto;
import com.internship.stocks_api.errors.CompanyInfoErrors;
import com.internship.stocks_api.errors.FinnhubApiErrors;
import com.internship.stocks_api.mappers.CompanyInfoMapper;
import com.internship.stocks_api.mappers.CompanyPeersMapper;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.models.CompanyPeer;
import com.internship.stocks_api.repositories.CompanyInfoRepository;
import com.internship.stocks_api.repositories.CompanyPeerRepository;
import com.internship.stocks_api.repositories.CompanyStockInfoRepository;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompanyPeerServiceTests {

    @Mock
    private CompanyInfoRepository companyInfoRepository;

    @Mock
    private CompanyPeerRepository companyPeerRepository;

    @Mock
    private CompanyPeersMapper companyPeersMapper;

    @Mock
    private FinnhubClient finnhubClient;

    @InjectMocks
    private CompanyPeerService service;

    @BeforeEach
    void setUp() {
        companyInfoRepository = mock(CompanyInfoRepository.class);
        companyPeerRepository = mock(CompanyPeerRepository.class);
        companyPeersMapper = mock(CompanyPeersMapper.class);
        finnhubClient = mock(FinnhubClient.class);
        service = new CompanyPeerService(
                companyInfoRepository,
                companyPeerRepository,
                companyPeersMapper,
                finnhubClient
        );
    }

    @Test
    void getPeersBySymbol_Should_ReturnFailure_WhenCompanyDoesNotExist() {
        // arrange
        String symbol = "SOME";
        when(companyInfoRepository.findBySymbol(symbol)).thenReturn(Optional.empty());

        // act
        Result<CompanyPeerViewDto> result = service.getPeersBySymbol(symbol);

        // assert
        assertTrue(result.isFailure());
        assertEquals(CompanyInfoErrors.symbolNotFound(symbol).code(), result.getError().code());
        assertEquals(CompanyInfoErrors.symbolNotFound(symbol).message(), result.getError().message());

        verifyNoInteractions(companyPeerRepository, finnhubClient, companyPeersMapper);
    }

    @Test
    void getPeersBySymbol_Should_ReturnSuccess_WhenPeersExistInDb() {
        // arrange
        String symbol = "SOME";

        var company = new CompanyInfo();
        company.setId(1L);
        company.setName("Some Company");
        company.setSymbol(symbol);

        List<String> existingPeers = List.of("PEER1");

        when(companyInfoRepository.findBySymbol(symbol)).thenReturn(Optional.of(company));
        when(companyPeerRepository.findPeerSymbolsByCompanySymbol(symbol)).thenReturn(existingPeers);
        when(companyPeersMapper.toViewDto(company, existingPeers))
                .thenReturn(new CompanyPeerViewDto(symbol, existingPeers));

        // act
        Result<CompanyPeerViewDto> result = service.getPeersBySymbol(symbol);

        // assert
        assertTrue(result.isSuccess());
        assertEquals(symbol, result.getValue().getSymbol());
        assertEquals(1, result.getValue().getPeers().size());
        assertEquals("PEER1", result.getValue().getPeers().get(0));

        verify(finnhubClient, never()).getCompanyPeers(any());
        verify(companyPeerRepository, never()).saveAll(any());
    }

    @Test
    void getPeersBySymbol_Should_CallFinnhubAndReturnSuccess_WhenPeersDoNotExistInDb() {
        // arrange
        String symbol = "SOME";

        var company = new CompanyInfo();
        company.setId(1L);
        company.setName("Some Company");
        company.setSymbol(symbol);

        List<String> emptyPeers = List.of();
        List<String> peersFromApi = List.of("PEER1", "PEER2");

        when(companyInfoRepository.findBySymbol(symbol)).thenReturn(Optional.of(company));
        when(companyPeerRepository.findPeerSymbolsByCompanySymbol(symbol)).thenReturn(emptyPeers);
        when(finnhubClient.getCompanyPeers(symbol)).thenReturn(peersFromApi);
        when(companyPeersMapper.toViewDto(company, peersFromApi))
                .thenReturn(new CompanyPeerViewDto(symbol, peersFromApi));

        // act
        Result<CompanyPeerViewDto> result = service.getPeersBySymbol(symbol);

        // assert
        assertTrue(result.isSuccess());
        assertEquals(symbol, result.getValue().getSymbol());
        assertEquals(2, result.getValue().getPeers().size());
        assertEquals("PEER1", result.getValue().getPeers().get(0));
        assertEquals("PEER2", result.getValue().getPeers().get(1));

        verify(finnhubClient, times(1)).getCompanyPeers(symbol);
        verify(companyPeerRepository, times(1)).saveAll(any());
    }

    @Test
    void getPeersBySymbol_Should_ReturnFailure_WhenFinnhubCallFails() {
        // arrange
        String symbol = "SOME";

        var company = new CompanyInfo();
        company.setId(1L);
        company.setName("Some Company");
        company.setSymbol(symbol);

        List<String> emptyPeers = List.of();

        when(companyInfoRepository.findBySymbol(symbol)).thenReturn(Optional.of(company));
        when(companyPeerRepository.findPeerSymbolsByCompanySymbol(symbol)).thenReturn(emptyPeers);
        when(finnhubClient.getCompanyPeers(symbol))
                .thenThrow(new RuntimeException("API down"));

        // act
        Result<CompanyPeerViewDto> result = service.getPeersBySymbol(symbol);

        // assert
        assertTrue(result.isFailure());
        assertEquals(FinnhubApiErrors.notFound(symbol).code(), result.getError().code());
        assertEquals(FinnhubApiErrors.notFound(symbol).message(), result.getError().message());

        verify(companyPeerRepository, never()).saveAll(any());
    }
}
