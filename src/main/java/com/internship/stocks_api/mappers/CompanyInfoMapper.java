package com.internship.stocks_api.mappers;

import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoUpdateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.models.CompanyInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyInfoMapper {
    CompanyInfoViewDto toViewDto(CompanyInfo companyInfo);
    CompanyInfo createDtoToEntity(CompanyInfoCreateDto dto);
    CompanyInfo updateDtoToEntity(CompanyInfoUpdateDto dto);
}
