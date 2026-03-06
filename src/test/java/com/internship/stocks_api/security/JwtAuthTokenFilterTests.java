package com.internship.stocks_api.security;

import com.internship.stocks_api.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthTokenFilterTests {

    @InjectMocks
    private JwtAuthTokenFilter jwtFilter;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext(); // clean context before each test
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_WhenTokenIsValid() throws Exception {
        // arrange
        String token = "valid.token.here";
        Long userId = 1L;
        var userDetails = mock(org.springframework.security.core.userdetails.UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateJwtToken(token)).thenReturn(true);
        when(tokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(userDetailsService.loadUserById(userId)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        // act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(userDetails, auth.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenTokenIsInvalid() throws Exception {
        // arrange
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateJwtToken(token)).thenReturn(false);

        // act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenNoHeader() throws Exception {
        // arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void getTokenFromRequest_ShouldExtractTokenCorrectly() throws NoSuchMethodException {
        when(request.getHeader("Authorization")).thenReturn("Bearer my.jwt.token");
        var method = jwtFilter.getClass().getDeclaredMethod("getTokenFromRequest", HttpServletRequest.class);
        method.setAccessible(true);
        try {
            String extracted = (String) method.invoke(jwtFilter, request);
            assertEquals("my.jwt.token", extracted);
        } catch (Exception e) {
            fail(e);
        }
    }
}