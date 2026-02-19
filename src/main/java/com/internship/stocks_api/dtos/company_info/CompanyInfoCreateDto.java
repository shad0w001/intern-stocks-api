package com.internship.stocks_api.dtos.company_info;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoCreateDto {
    @NotBlank
    private String name;

    @NotBlank
    @Size(min = 2, max = 2)
    private String country;

    @NotBlank
    private String symbol;

    @URL
    private String website;

    @Email
    private String email;
}
