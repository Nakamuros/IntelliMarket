package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
}