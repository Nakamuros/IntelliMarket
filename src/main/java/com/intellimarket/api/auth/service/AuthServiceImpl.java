package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.LoginRequest;
import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.exception.EmailAlreadyExistsException;
import com.intellimarket.api.auth.exception.InvalidCredentialsException;
import com.intellimarket.api.auth.mapper.AuthMapper;
import com.intellimarket.api.auth.model.Role; // <-- Importación del Rol
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
    public AuthResponse registerCustomer(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = authMapper.toUser(request);
        user.setRole(Role.CUSTOMER); // Asignación automática para cliente

        User savedUser = userRepository.save(user);
        return authMapper.toAuthResponse(savedUser);
    }

    @Override
    public AuthResponse registerOwner(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = authMapper.toUser(request);
        user.setRole(Role.OWNER); // Asignación automática para dueño de bodega

        User savedUser = userRepository.save(user);
        return authMapper.toAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        //Buscar al usuario por correo
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        //Verificar la contraseña
        if (!user.getPassword().equals(request.password())) {
            throw new InvalidCredentialsException();
        }

        //Devolver los datos del usuario
        return authMapper.toAuthResponse(user);
    }

    @Override
    public AuthResponse registerAdmin(RegisterRequest request) {
        //La validación de seguridad (Tu idea del dominio)
        if (!request.getEmail().endsWith("@admin.com")) {
            throw new RuntimeException("Acceso denegado: Solo se permiten correos del dominio @admin.com");
        }

        //Validación de duplicados
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        //Creación del usuario fantasma (solo credenciales)
        User user = authMapper.toUser(request);
        user.setRole(Role.ADMIN);

        User savedUser = userRepository.save(user);
        return authMapper.toAuthResponse(savedUser);
    }
}