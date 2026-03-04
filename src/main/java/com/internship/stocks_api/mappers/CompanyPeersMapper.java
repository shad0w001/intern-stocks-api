package com.internship.stocks_api.mappers;

import com.internship.stocks_api.dtos.company_peers.CompanyPeerViewDto;
import com.internship.stocks_api.models.CompanyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyPeersMapper {

    @Mapping(target = "symbol", source = "company.symbol")
    @Mapping(target = "peers", source = "peers")
    CompanyPeerViewDto toViewDto(CompanyInfo company, List<String> peers);
}
