package com.internship.stocks_api.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.verify;

class JwtAuthEntryPointTests {

    private final JwtAuthEntryPoint entryPoint = new JwtAuthEntryPoint();

    @Test
    void commence_ShouldSendUnauthorizedError() throws IOException, ServletException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        AuthenticationException authException = Mockito.mock(AuthenticationException.class);

        entryPoint.commence(request, response, authException);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "401 Unauthorized");
    }
}