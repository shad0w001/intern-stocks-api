package com.internship.stocks_api.controllers;

import com.internship.stocks_api.dtos.company_peers.CompanyPeerViewDto;
import com.internship.stocks_api.services.CompanyPeerService;
import com.internship.stocks_api.shared.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyPeerController {

    private final CompanyPeerService service;

    @GetMapping("/{symbol}/peers")
    public ResponseEntity<Object> getCompanyPeers(@PathVariable String symbol) {

        Result<CompanyPeerViewDto> result = service.getPeersBySymbol(symbol);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getError().status())
                    .body(result.getError().message());
        }

        return ResponseEntity.ok(result.getValue());
    }
}
