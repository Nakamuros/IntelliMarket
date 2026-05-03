package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.LoginRequest;
import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;

public interface IAuthService {
    AuthResponse registerCustomer(RegisterRequest request);
    AuthResponse registerOwner(RegisterRequest request);
    AuthResponse registerAdmin(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}