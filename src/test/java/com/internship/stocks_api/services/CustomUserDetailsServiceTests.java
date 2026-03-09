package com.internship.stocks_api.services;

import com.internship.stocks_api.models.User;
import com.internship.stocks_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hashed_pwd");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("hashed_pwd", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // arrange
        String email = "missing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });
    }

    @Test
    void loadUserById_ShouldReturnUserDetails_WhenIdExists() {
        // arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("user@id.com");
        user.setPasswordHash("secret");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // act
        UserDetails userDetails = userDetailsService.loadUserById(userId);

        // assert
        assertEquals("user@id.com", userDetails.getUsername());
        verify(userRepository).findById(userId);
    }

    @Test
    void loadUserById_ShouldThrowException_WhenIdDoesNotExist() {
        // arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // act & assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserById(userId);
        });

        assertTrue(exception.getMessage().contains("id: 99"));
    }
}
