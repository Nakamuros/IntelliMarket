package com.intellimarket.api.auth.service;

import com.intellimarket.api.auth.dto.LoginRequest;
import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.exception.EmailAlreadyExistsException;
import com.intellimarket.api.auth.exception.InvalidCredentialsException;
import com.intellimarket.api.auth.mapper.AuthMapper;
import com.intellimarket.api.auth.model.RefreshToken;
import com.intellimarket.api.auth.model.Role;
import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import com.intellimarket.api.profile.repository.CustomerRepository;
import com.intellimarket.api.profile.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    // Inyectamos los nuevos repositorios
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional // Asegura que se guarden AMBAS tablas o ninguna
    public AuthResponse registerCustomer(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = authMapper.toUser(request);
        // NUEVO: Encriptamos la contraseña antes de asignarle el rol
        user.setPassword(passwordEncoder.encode(request.getPassword()));

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
        // NUEVO: Encriptamos la contraseña
        user.setPassword(passwordEncoder.encode(request.getPassword()));

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
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        // Los admins no tienen perfil según el diagrama, así que solo guardamos el User
        User savedUser = userRepository.save(user);
        return authMapper.toAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // 1. Generamos el Access Token (15 min)
        String accessToken = jwtService.generateToken(userDetails, user.getRole().name());

        // 2. Generamos el Refresh Token (7 días) y se guarda en la BD
        RefreshToken refreshToken = refreshTokenService.create(user);

        // 3. Devolvemos AMBOS tokens
        return AuthResponse.fromUser(user, accessToken, refreshToken.getToken());
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        // 1. Validamos que el refresh token exista, no esté expirado y no esté revocado
        RefreshToken currentToken = refreshTokenService.validate(refreshToken);

        // 2. Aplicamos la "Rotación" (Matamos el viejo y creamos uno nuevo)
        RefreshToken rotatedToken = refreshTokenService.rotate(currentToken);

        // 3. Generamos el nuevo Access Token (para otros 15 minutos)
        User user = rotatedToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateToken(userDetails, user.getRole().name());

        // 4. Devolvemos las dos llaves NUEVAS
        return AuthResponse.fromUser(user, newAccessToken, rotatedToken.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        // Marcamos el Refresh Token como revocado (revoked = true)
        refreshTokenService.revoke(refreshToken);
    }

    //@Override
    //public void changePassword(String email, ChangePasswordRequest request) {
    //    User user = userRepository.findByEmail(email)
    //          .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    //  if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
    //      throw new InvalidCredentialsException();
    //}
    //
    //    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    //    userRepository.save(user);
}