package com.internship.stocks_api.controllers;

import com.internship.stocks_api.services.CompanyStockInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
public class CompanyStockInfoController {

    private final CompanyStockInfoService service;

    public CompanyStockInfoController(CompanyStockInfoService service) {
        this.service = service;
    }

    @GetMapping("company-stocks/{id}")
    public ResponseEntity<?> getCompanyStockInfo(@PathVariable Long id){

        var result = service.getCompanyStockInfo(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getError().status())
                    .body(result.getError().message());
        }

        return ResponseEntity.ok(result.getValue());
    }
}
