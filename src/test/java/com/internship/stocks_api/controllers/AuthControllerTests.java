package com.internship.stocks_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.stocks_api.dtos.auth.LoginRequestDto;
import com.internship.stocks_api.dtos.auth.LoginResponseDto;
import com.internship.stocks_api.dtos.auth.RegisterRequestDto;
import com.internship.stocks_api.errors.AuthErrors;
import com.internship.stocks_api.security.JwtAuthTokenFilter;
import com.internship.stocks_api.services.AuthService;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthTokenFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "finnhub.client.type=rest",
        "finnhub.api.base-url=http://localhost",
        "finnhub.api.key=dummy"
})
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private AuthService authService;

    @Test
    void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
        // arrange
        var dto = new LoginRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("password");

        var responseDto = new LoginResponseDto("jwtToken", "Bearer", 1L);

        when(authService.login(dto)).thenReturn(Result.success(responseDto));

        String requestJson = objectMapper.writeValueAsString(dto);
        String responseJson = objectMapper.writeValueAsString(responseDto);

        // act + assert
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        var dto = new LoginRequestDto(); // empty email/password

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenUserNotFound() throws Exception {
        // arrange
        var dto = new LoginRequestDto();
        dto.setEmail("unknown@example.com");
        dto.setPassword("password");

        when(authService.login(dto)).thenReturn(Result.failure(AuthErrors.notFound(dto.getEmail())));

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(AuthErrors.notFound(dto.getEmail()).message()));
    }

    @Test
    void register_ShouldReturnOk_WhenUserIsNew() throws Exception {
        // arrange
        var dto = new RegisterRequestDto();
        dto.setEmail("newuser@example.com");
        dto.setPassword("password");

        when(authService.register(dto)).thenReturn(Result.success("User registered successfully"));

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUserAlreadyExists() throws Exception {
        // arrange
        var dto = new RegisterRequestDto();
        dto.setEmail("existing@example.com");
        dto.setPassword("password");

        when(authService.register(dto)).thenReturn(Result.failure(AuthErrors.alreadyExists(dto.getEmail())));

        String requestJson = objectMapper.writeValueAsString(dto);

        // act + assert
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(AuthErrors.alreadyExists(dto.getEmail()).message()));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        var dto = new RegisterRequestDto(); // empty email/password

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}