package com.internship.stocks_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_peers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "symbol"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyPeer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyInfo company;
}
