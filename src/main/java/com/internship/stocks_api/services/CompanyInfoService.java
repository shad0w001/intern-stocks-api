package com.internship.stocks_api.services;

import com.internship.stocks_api.clients.FinnhubClient;
import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoUpdateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.errors.CompanyInfoErrors;
import com.internship.stocks_api.mappers.CompanyInfoMapper;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.repositories.CompanyInfoRepository;
import com.internship.stocks_api.repositories.CompanyStockInfoRepository;
import com.internship.stocks_api.shared.Result;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyInfoService {

    private final CompanyInfoRepository companyInfoRepository;
    private final CompanyStockInfoRepository companyStockInfoRepository;
    private final CompanyInfoMapper companyInfoMapper;
    private final FinnhubClient finnhubClient;

    public Result<List<CompanyInfoViewDto>> getAllCompanyInfoEntries(){
        var list = companyInfoRepository.findAll()
                .stream()
                .map(companyInfoMapper::toViewDto)
                .toList();

        return Result.success(list);
    }

    public Result<CompanyInfoViewDto> getCompanyInfoEntry(Long id){
        var companyInfo = companyInfoRepository.findById(id).orElse(null);

        if (companyInfo == null) {
            return Result.failure(CompanyInfoErrors.notFound(id));
        }

        return Result.success(companyInfoMapper.toViewDto(companyInfo));
    }

    public Result<CompanyInfoViewDto> createCompanyInfoEntry(CompanyInfoCreateDto dto) {

        if (companyInfoRepository.findBySymbol(dto.getSymbol()).isPresent()) {
            return Result.failure(
                    CompanyInfoErrors.symbolAlreadyExists(dto.getSymbol())
            );
        }

        CompanyInfo entity = companyInfoMapper.createDtoToEntity(dto);
        var saved = companyInfoRepository.save(entity);

        return Result.success(companyInfoMapper.toViewDto(saved));
    }

    public Result<CompanyInfoViewDto> updateCompanyInfoEntry(Long id, CompanyInfoUpdateDto dto) {

        var existing = companyInfoRepository.findById(id).orElse(null);

        if (existing == null) {
            return Result.failure(CompanyInfoErrors.notFound(id));
        }

        companyInfoMapper.updateDtoToEntity(dto, existing);
        var saved = companyInfoRepository.save(existing);

        return Result.success(companyInfoMapper.toViewDto(saved));
    }
}
