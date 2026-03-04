package com.internship.stocks_api.repositories;

import com.internship.stocks_api.models.CompanyPeer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyPeerRepository extends JpaRepository<CompanyPeer, Long> {

    @Query("""
        select cp.symbol
        from CompanyPeer cp
        where cp.company.symbol = :symbol
    """)
    List<String> findPeerSymbolsByCompanySymbol(String symbol);
}
