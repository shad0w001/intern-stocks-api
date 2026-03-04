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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyPeerService {

    private final CompanyInfoRepository companyInfoRepository;
    private final CompanyPeerRepository companyPeerRepository;
    private final CompanyPeersMapper companyPeersMapper;
    private final FinnhubClient finnhubClient;

    public Result<CompanyPeerViewDto> getPeersBySymbol(String symbol){

        var companyInfo = companyInfoRepository.findBySymbol(symbol).orElse(null);

        if(companyInfo == null){
            return Result.failure(CompanyInfoErrors.symbolNotFound(symbol));
        }

        List<String> peers = companyPeerRepository.findPeerSymbolsByCompanySymbol(symbol);

        if(peers.isEmpty()){
            try{
                var peersFromApi = finnhubClient.getCompanyPeers(symbol);

                Set<CompanyPeer> peerEntities = new HashSet<>();
                for (String companySymbol : peersFromApi) {
                    CompanyPeer peer = new CompanyPeer();
                    peer.setSymbol(companySymbol);
                    peer.setCompany(companyInfo);
                    peerEntities.add(peer);
                }
                companyPeerRepository.saveAll(peerEntities);
            }catch (Exception ex){
                return Result.failure(FinnhubApiErrors.notFound(symbol));
            }
        }

        CompanyPeerViewDto dto = companyPeersMapper.toViewDto(companyInfo, peers);
        return Result.success(dto);
    }

}
