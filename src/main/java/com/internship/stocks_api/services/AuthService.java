package com.internship.stocks_api.services;

import com.internship.stocks_api.dtos.auth.LoginRequestDto;
import com.internship.stocks_api.dtos.auth.LoginResponseDto;
import com.internship.stocks_api.dtos.auth.RegisterRequestDto;
import com.internship.stocks_api.errors.AuthErrors;
import com.internship.stocks_api.models.User;
import com.internship.stocks_api.repositories.UserRepository;
import com.internship.stocks_api.security.JwtTokenProvider;
import com.internship.stocks_api.shared.ApiError;
import com.internship.stocks_api.shared.Result;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public Result<LoginResponseDto> login(LoginRequestDto request){
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword());
            authenticationManager.authenticate(authToken);

            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                    () -> new AuthenticationCredentialsNotFoundException(
                            "User with the email " + request.getEmail() + " was not found."));

            String jwt = jwtTokenProvider.generateToken(user.getId());

            return Result.success(new LoginResponseDto(jwt, "Bearer", user.getId()));
        } catch (AuthenticationCredentialsNotFoundException ex){
            return Result.failure(AuthErrors.notFound(request.getEmail()));
        }
        catch (Exception ex){
            return Result.failure(AuthErrors.authProblem(ex.getMessage()));
        }
    }

    public Result<String> register(RegisterRequestDto request){
        if(userRepository.existsByEmail(request.getEmail())){
            return Result.failure(AuthErrors.alreadyExists(request.getEmail()));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        return Result.success("User registered successfully");
    }
}
