package com.internship.stocks_api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 4)
    private String password;
}
