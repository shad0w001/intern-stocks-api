package com.internship.stocks_api.services;

import com.internship.stocks_api.dtos.auth.LoginRequestDto;
import com.internship.stocks_api.dtos.auth.LoginResponseDto;
import com.internship.stocks_api.dtos.auth.RegisterRequestDto;
import com.internship.stocks_api.errors.AuthErrors;
import com.internship.stocks_api.models.User;
import com.internship.stocks_api.repositories.UserRepository;
import com.internship.stocks_api.security.JwtTokenProvider;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        authenticationManager = mock(AuthenticationManager.class);

        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider, authenticationManager);
    }

    @Test
    void register_ShouldReturnSuccess_WhenUserDoesNotExist() {
        // arrange
        var dto = new RegisterRequestDto();
        dto.setEmail("newuser@example.com");
        dto.setPassword("password123");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        var savedUser = new User();
        savedUser.setEmail(dto.getEmail());
        savedUser.setPasswordHash("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // act
        Result<String> result = authService.register(dto);

        // assert
        assertTrue(result.isSuccess());
        assertEquals("User registered successfully", result.getValue());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldReturnFailure_WhenUserAlreadyExists() {
        // arrange
        var dto = new RegisterRequestDto();
        dto.setEmail("existing@example.com");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // act
        Result<String> result = authService.register(dto);

        // assert
        assertTrue(result.isFailure());
        assertEquals(AuthErrors.alreadyExists(dto.getEmail()).code(), result.getError().code());
        assertEquals(AuthErrors.alreadyExists(dto.getEmail()).message(), result.getError().message());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnSuccess_WhenCredentialsAreValid() throws Exception {
        // arrange
        var dto = new LoginRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("password");

        var user = new User();
        user.setId(1L);
        user.setEmail(dto.getEmail());
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(jwtTokenProvider.generateToken(user.getId())).thenReturn("jwtToken");

        // act
        Result<LoginResponseDto> result = authService.login(dto);

        // assert
        assertTrue(result.isSuccess());
        assertEquals("jwtToken", result.getValue().getAccessToken());
        assertEquals("Bearer", result.getValue().getTokenType());
        assertEquals(user.getId(), result.getValue().getUserId());
    }

    @Test
    void login_ShouldReturnFailure_WhenUserNotFound() throws Exception {
        // arrange
        var dto = new LoginRequestDto();
        dto.setEmail("nonexistent@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        doThrow(new AuthenticationCredentialsNotFoundException("User not found"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // act
        Result<LoginResponseDto> result = authService.login(dto);

        // assert
        assertTrue(result.isFailure());
        assertEquals(AuthErrors.notFound(dto.getEmail()).code(), result.getError().code());
        assertEquals(AuthErrors.notFound(dto.getEmail()).message(), result.getError().message());
    }

    @Test
    void login_ShouldReturnFailure_WhenAuthenticationFails() throws Exception {
        // arrange
        var dto = new LoginRequestDto();
        dto.setEmail("user@example.com");
        dto.setPassword("wrongpassword");

        doThrow(new RuntimeException("Some auth error"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // act
        Result<LoginResponseDto> result = authService.login(dto);

        // assert
        assertTrue(result.isFailure());
        assertEquals(AuthErrors.authProblem("Some auth error").code(), result.getError().code());
        assertEquals(AuthErrors.authProblem("Some auth error").message(), result.getError().message());
    }
}