package com.internship.stocks_api.controllers;

import com.internship.stocks_api.dtos.auth.LoginRequestDto;
import com.internship.stocks_api.dtos.auth.RegisterRequestDto;
import com.internship.stocks_api.services.AuthService;
import com.internship.stocks_api.shared.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @Valid @RequestBody LoginRequestDto dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Result<?> result = authService.login(dto);

        if (result.isFailure()) {
            return ResponseEntity.status(401).body(result.getError().message());
        }

        return ResponseEntity.ok(result.getValue());
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody RegisterRequestDto dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        Result<?> result = authService.register(dto);

        if (result.isFailure()) {
            return ResponseEntity.badRequest().body(result.getError().message());
        }

        return ResponseEntity.ok(result.getValue());
    }
}