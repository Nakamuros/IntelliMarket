package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.exception.BusinessException;
import com.intellimarket.api.auth.mapper.AuthMapper;
import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo ya está registrado.");
        }

        User user = authMapper.toUser(request);
        User savedUser = userRepository.save(user);
        return authMapper.toAuthResponse(savedUser);
    }
}