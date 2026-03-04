package com.internship.stocks_api.dtos.company_peers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPeerViewDto {
    private String symbol;
    private List<String> peers;
}
