package com.internship.stocks_api.services;

import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoUpdateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.mappers.CompanyInfoMapper;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.repositories.CompanyInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyInfoService {

    private final CompanyInfoRepository companyInfoRepository;
    private final CompanyInfoMapper companyInfoMapper;

    public List<CompanyInfoViewDto> getAllCompanyInfoEntries(){
        return companyInfoRepository.findAll()
                .stream()
                .map(companyInfoMapper::toViewDto)
                .toList();
    }

    public CompanyInfoViewDto createCompanyInfoEntry(CompanyInfoCreateDto dto){
        var alreadyExistingInfo = companyInfoRepository.findBySymbol(dto.getSymbol())
                .orElse(null);

        if(alreadyExistingInfo != null){
            return null;
        }

        CompanyInfo entry = companyInfoMapper.createDtoToEntity(dto);
        var saved = companyInfoRepository.save(entry);

        return companyInfoMapper.toViewDto(saved);
    }

    public CompanyInfoViewDto updateCompanyInfoEntry(Long id, CompanyInfoUpdateDto dto){
        var existing = companyInfoRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        companyInfoMapper.updateDtoToEntity(dto, existing);

        var saved = companyInfoRepository.save(existing);
        return companyInfoMapper.toViewDto(saved);
    }
}
