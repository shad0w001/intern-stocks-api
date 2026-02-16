package com.internship.stocks_api.controllers;

import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.models.CompanyInfo;
import com.internship.stocks_api.services.CompanyInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyInfoController {

    private final CompanyInfoService service;

    public CompanyInfoController(CompanyInfoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CompanyInfoViewDto>> getAllCompanyInfoEntries(){
        var result = service.getAllCompanyInfoEntries();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createCompanyEntry(@Valid @RequestBody CompanyInfoCreateDto dto){
        var companyInfo = service.createCompanyInfoEntry(dto);
        if(companyInfo == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A company with this symbol already exists");
        }
        return ResponseEntity.ok(companyInfo);
    }

}
