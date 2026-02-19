package com.internship.stocks_api.repositories;

import com.internship.stocks_api.models.CompanyStockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CompanyStockInfoRepository extends JpaRepository<CompanyStockInfo, Long> {
    Optional<CompanyStockInfo> findBySymbolAndDate(String symbol, LocalDate date);
}
