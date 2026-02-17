package com.internship.stocks_api.mappers;

import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoUpdateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.models.CompanyInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyInfoMapper {
    CompanyInfoViewDto toViewDto(CompanyInfo companyInfo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CompanyInfo createDtoToEntity(CompanyInfoCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateDtoToEntity(CompanyInfoUpdateDto dto, @MappingTarget CompanyInfo entity);
}
