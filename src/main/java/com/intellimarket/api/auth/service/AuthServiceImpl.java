package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.LoginRequest;
import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.exception.EmailAlreadyExistsException;
import com.intellimarket.api.auth.exception.InvalidCredentialsException;
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
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = authMapper.toUser(request);
        User savedUser = userRepository.save(user);
        return authMapper.toAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Buscar al usuario por correo
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        // 2. Verificar la contraseña (por ahora en texto plano)
        if (!user.getPassword().equals(request.password())) {
            throw new InvalidCredentialsException();
        }

        // 3. Devolver los datos del usuario si todo coincide
        return authMapper.toAuthResponse(user);
    }
}