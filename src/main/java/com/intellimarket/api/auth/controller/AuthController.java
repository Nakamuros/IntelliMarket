package com.intellimarket.api.auth.controller;

import com.intellimarket.api.auth.dto.LoginRequest;
import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/register/customer")
    public ResponseEntity<AuthResponse> registerCustomer(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.registerCustomer(request), HttpStatus.CREATED);
    }
