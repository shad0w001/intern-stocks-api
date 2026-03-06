package com.internship.stocks_api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

    @Data
    public class LoginRequestDto {

        @Email
        @NotEmpty
        private String email;

        @NotEmpty
        @Size(min = 4)
        private String password;
    }
