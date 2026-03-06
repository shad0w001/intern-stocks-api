package com.internship.stocks_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTests {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() throws Exception {
        jwtTokenProvider = new JwtTokenProvider();

        setPrivateField(jwtTokenProvider, "jwtSecret", "01234567890123456789012345678901"); // 32 chars for HS256
        setPrivateField(jwtTokenProvider, "jwtExpirationInMs", 3600000L); // 1 hour

        jwtTokenProvider.init();
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtTokenProvider.generateToken(123L);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserIdFromToken_ShouldReturnCorrectUserId() {
        String token = jwtTokenProvider.generateToken(123L);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals(123L, userId);
    }

    @Test
    void validateJwtToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtTokenProvider.generateToken(123L);
        assertTrue(jwtTokenProvider.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_ShouldReturnFalse_ForInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtTokenProvider.validateJwtToken(invalidToken));
    }

    @Test
    void validateJwtToken_ShouldReturnFalse_ForNullToken() {
        assertFalse(jwtTokenProvider.validateJwtToken(null));
    }

    @Test
    void validateJwtToken_ShouldReturnFalse_ForEmptyToken() {
        assertFalse(jwtTokenProvider.validateJwtToken(""));
    }
}