package com.internship.stocks_api.services.oauth2.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.stocks_api.services.oauth2.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthService oauthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String provider = "google";
        String providerUserId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");

        var loginResult = oauthService.loginOrRegister(provider, providerUserId, email);

        if (loginResult.isSuccess()) {
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(loginResult.getValue()));
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, loginResult.getError().message());
        }
    }
}
