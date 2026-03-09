package com.internship.stocks_api.services;

import com.internship.stocks_api.dtos.auth.LoginResponseDto;
import com.internship.stocks_api.models.OAuthAccount;
import com.internship.stocks_api.models.User;
import com.internship.stocks_api.repositories.OAuthAccountRepository;
import com.internship.stocks_api.repositories.UserRepository;
import com.internship.stocks_api.security.JwtTokenProvider;
import com.internship.stocks_api.services.oauth2.OAuthService;
import com.internship.stocks_api.shared.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OAuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuthAccountRepository oauthAccountRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private OAuthService oauthService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        oauthAccountRepository = mock(OAuthAccountRepository.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);

        oauthService = new OAuthService(userRepository, oauthAccountRepository, jwtTokenProvider);
    }

    @Test
    void loginOrRegister_ShouldReturnSuccess_WhenOAuthAccountExists() {
        // arrange
        String provider = "google";
        String providerId = "12345";

        User existingUser = new User();
        existingUser.setId(10L);

        OAuthAccount account = new OAuthAccount();
        account.setUser(existingUser);

        when(oauthAccountRepository.findByProviderAndProviderUserId(provider, providerId))
                .thenReturn(Optional.of(account));
        when(jwtTokenProvider.generateToken(existingUser.getId())).thenReturn("fake-jwt");

        // act
        Result<LoginResponseDto> result = oauthService.loginOrRegister(provider, providerId, "user@test.com");

        // assert
        assertTrue(result.isSuccess());
        assertEquals("fake-jwt", result.getValue().getAccessToken());
        assertEquals(10L, result.getValue().getUserId());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginOrRegister_ShouldLinkExistingUser_WhenEmailMatchesButOAuthDoesntExist() {
        // arrange
        String provider = "google";
        String providerId = "999";
        String email = "existing@test.com";

        User existingUser = new User();
        existingUser.setId(5L);
        existingUser.setEmail(email);

        when(oauthAccountRepository.findByProviderAndProviderUserId(provider, providerId))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(jwtTokenProvider.generateToken(existingUser.getId())).thenReturn("fake-jwt");

        // act
        Result<LoginResponseDto> result = oauthService.loginOrRegister(provider, providerId, email);

        // assert
        assertTrue(result.isSuccess());
        verify(oauthAccountRepository).save(any(OAuthAccount.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginOrRegister_ShouldCreateNewUserAndAccount_WhenNoMatchFound() {
        // arrange
        String provider = "google";
        String providerId = "new_uid";
        String email = "new@test.com";

        when(oauthAccountRepository.findByProviderAndProviderUserId(provider, providerId))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Mock save to return a user with an ID (simulating DB behavior)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenProvider.generateToken(1L)).thenReturn("new-jwt");

        // act
        Result<LoginResponseDto> result = oauthService.loginOrRegister(provider, providerId, email);

        // assert
        assertTrue(result.isSuccess());
        verify(userRepository).save(any(User.class));
        verify(oauthAccountRepository).save(any(OAuthAccount.class));
        assertEquals(1L, result.getValue().getUserId());
    }

    @Test
    void loginOrRegister_ShouldReturnFailure_WhenProviderIdIsNull() {
        // act
        Result<LoginResponseDto> result = oauthService.loginOrRegister("google", null, "test@test.com");

        // assert
        assertTrue(result.isFailure());
        assertTrue(result.getError().message().contains("must not be null"));
    }

    @Test
    void loginOrRegister_ShouldHandleExceptions_AndReturnFailure() {
        // arrange
        when(oauthAccountRepository.findByProviderAndProviderUserId(any(), any()))
                .thenThrow(new RuntimeException("Database down"));

        // act
        Result<LoginResponseDto> result = oauthService.loginOrRegister("google", "123", "email");

        // assert
        assertTrue(result.isFailure());
        assertEquals("There was a problem with the OAuth authentication: Database down", result.getError().message());
    }
}