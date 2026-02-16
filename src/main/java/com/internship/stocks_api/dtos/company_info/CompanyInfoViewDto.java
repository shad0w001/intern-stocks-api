package com.internship.stocks_api.dtos.company_info;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoViewDto {
    private Long id;

    private String name;

    private String country;

    private String symbol;

    private String website;

    private String email;

    private LocalDateTime createdAt;
}
