package com.internship.stocks_api.security.oauth2.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.stocks_api.dtos.auth.LoginResponseDto;
import com.internship.stocks_api.errors.OAuthErrors;
import com.internship.stocks_api.services.oauth2.OAuthService;
import com.internship.stocks_api.services.oauth2.handlers.GoogleOAuth2SuccessHandler;
import com.internship.stocks_api.shared.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GoogleOAuth2SuccessHandlerTests {

    @Mock
    private OAuthService oauthService;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    private GoogleOAuth2SuccessHandler handler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        oauthService = mock(OAuthService.class);
        authentication = mock(Authentication.class);
        oAuth2User = mock(OAuth2User.class);

        handler = new GoogleOAuth2SuccessHandler(oauthService);
    }

    @Test
    void onAuthenticationSuccess_ShouldReturnJson_WhenLoginIsSuccessful() throws IOException {
        // arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String email = "test@gmail.com";
        String sub = "google-id-123";

        // Mocking the OAuth2User attributes
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("sub")).thenReturn(sub);
        when(oAuth2User.getAttribute("email")).thenReturn(email);

        LoginResponseDto loginResponse = new LoginResponseDto("jwt-token", "Bearer", 1L);
        when(oauthService.loginOrRegister("google", sub, email))
                .thenReturn(Result.success(loginResponse));

        // act
        handler.onAuthenticationSuccess(request, response, authentication);

        // assert
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("application/json", response.getContentType());

        String expectedJson = objectMapper.writeValueAsString(loginResponse);
        assertEquals(expectedJson, response.getContentAsString());
    }

    @Test
    void onAuthenticationSuccess_ShouldReturnUnauthorized_WhenLoginFails() throws IOException {
        // arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("sub")).thenReturn("123");
        when(oAuth2User.getAttribute("email")).thenReturn("fail@test.com");

        String errorMsg = "OAuth error occurred";
        when(oauthService.loginOrRegister(anyString(), anyString(), anyString()))
                .thenReturn(Result.failure(OAuthErrors.authProblem(errorMsg)));

        // act
        handler.onAuthenticationSuccess(request, response, authentication);

        // assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(OAuthErrors.authProblem(errorMsg).message(), response.getErrorMessage());
    }
}
