package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.LoginRequest;
import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.exception.EmailAlreadyExistsException;
import com.intellimarket.api.auth.exception.InvalidCredentialsException;
import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.intellimarket.api.auth.model.Role;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import com.intellimarket.api.profile.repository.CustomerRepository;
import com.intellimarket.api.profile.repository.OwnerRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();
        
        User savedUser = userRepository.save(user);

        // Crear perfil automáticamente según el rol
        if (savedUser.getRole() == Role.CUSTOMER) {
            Customer customer = Customer.builder()
                    .user(savedUser)
                    .build();
            customerRepository.save(customer);
        } else if (savedUser.getRole() == Role.SELLER) {
            Owner owner = Owner.builder()
                    .user(savedUser)
                    .build();
            ownerRepository.save(owner);
        }
        
        String token = jwtService.generateToken(savedUser);
        return AuthResponse.fromUser(savedUser, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Buscar al usuario por correo
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException());

        // 2. Verificar la contraseña hasheada
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // 3. Generar token y devolver respuesta
        String token = jwtService.generateToken(user);
        return AuthResponse.fromUser(user, token);
    }
}