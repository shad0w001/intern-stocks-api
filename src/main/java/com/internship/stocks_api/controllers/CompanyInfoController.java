package com.internship.stocks_api.controllers;

import com.internship.stocks_api.dtos.company_info.CompanyInfoCreateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoUpdateDto;
import com.internship.stocks_api.dtos.company_info.CompanyInfoViewDto;
import com.internship.stocks_api.services.CompanyInfoService;
import com.internship.stocks_api.shared.Result;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyInfoController {

    private final CompanyInfoService service;

    public CompanyInfoController(CompanyInfoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Object> getAllCompanyInfoEntries(){
        var result = service.getAllCompanyInfoEntries();
        return ResponseEntity.ok(result.getValue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompanyInfoEntry(@PathVariable Long id){

        var result = service.getCompanyInfoEntry(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getError().status())
                    .body(result.getError().message());
        }

        return ResponseEntity.ok(result.getValue());
    }

    @PostMapping
    public ResponseEntity<Object> createCompanyEntry
            (@Valid @RequestBody CompanyInfoCreateDto dto,
             BindingResult bindingResult,
             UriComponentsBuilder uriBuilder){

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Result<CompanyInfoViewDto> result = service.createCompanyInfoEntry(dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getError().status())
                    .body(result.getError().message());
        }

        var company = result.getValue();
        var uri = uriBuilder.path("/companies/{id}").buildAndExpand(company.getId()).toUri();

        return ResponseEntity.created(uri).body(company);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCompany
            (@PathVariable Long id,
             @Valid @RequestBody CompanyInfoUpdateDto dto,
             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Result<CompanyInfoViewDto> result = service.updateCompanyInfoEntry(id, dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getError().status())
                    .body(result.getError().message());
        }

        return ResponseEntity.noContent().build();
    }

}
