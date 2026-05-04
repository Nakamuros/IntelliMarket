package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.LoginRequest;
import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.exception.EmailAlreadyExistsException;
import com.intellimarket.api.auth.exception.InvalidCredentialsException;
import com.intellimarket.api.auth.mapper.AuthMapper;
import com.intellimarket.api.auth.model.Role;
import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import com.intellimarket.api.profile.repository.CustomerRepository;
import com.intellimarket.api.profile.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    // Inyectamos los nuevos repositorios
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    @Override
    @Transactional // Asegura que se guarden AMBAS tablas o ninguna
    public AuthResponse registerCustomer(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = authMapper.toUser(request);
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        // Creamos el perfil de cliente vacío vinculado al usuario
        Customer customer = Customer.builder()
                .user(savedUser)
                .build();
        customerRepository.save(customer);

        return authMapper.toAuthResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse registerOwner(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = authMapper.toUser(request);
        user.setRole(Role.OWNER);
        User savedUser = userRepository.save(user);

        // Creamos el perfil de dueño vacío vinculado al usuario
        Owner owner = Owner.builder()
                .user(savedUser)
                .build();
        ownerRepository.save(owner);

        return authMapper.toAuthResponse(savedUser);
    }

    @Override
    public AuthResponse registerAdmin(RegisterRequest request) {
        if (!request.getEmail().endsWith("@admin.com")) {
            throw new RuntimeException("Acceso denegado: Dominio no autorizado");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = authMapper.toUser(request);
        user.setRole(Role.ADMIN);
        // Los admins no tienen perfil según el diagrama, así que solo guardamos el User
        User savedUser = userRepository.save(user);
        return authMapper.toAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.getPassword().equals(request.password())) {
            throw new InvalidCredentialsException();
        }

        return authMapper.toAuthResponse(user);
    }
}