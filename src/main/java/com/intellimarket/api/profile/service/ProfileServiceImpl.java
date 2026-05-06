package com.intellimarket.api.profile.service;

import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.profile.dto.*;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import com.intellimarket.api.profile.repository.CustomerRepository;
import com.intellimarket.api.profile.repository.OwnerRepository;
import com.intellimarket.api.profile.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository; // ¡Asegúrate de inyectar este!
    private final ProfileMapper profileMapper;

    // --- CUSTOMER ---

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCustomerProfileByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        return profileMapper.toResponse(customer); // <-- Recuerda cambiar este nombre por el tuyo
    }

    @Override
    @Transactional
    public ProfileResponse updateCustomerProfileByEmail(String email, CustomerProfileRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        // Actualiza los campos que tengas en tu Customer (esto es un ejemplo)
        // customer.setPhone(request.getPhone());
        // customer.setAddress(request.getAddress());

        return profileMapper.toResponse(customerRepository.save(customer)); // <-- Cambia el nombre aquí también
    }

    // --- OWNER ---

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getOwnerProfileByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Owner owner = ownerRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        return profileMapper.toResponse(owner); // <-- Cambia el nombre aquí también
    }

    @Override
    @Transactional
    public ProfileResponse updateOwnerProfileByEmail(String email, OwnerProfileRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Owner owner = ownerRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Perfil no encontrado"));

        // Actualiza los campos que tengas en tu Owner
        // owner.setPhone(request.getPhone());
        // owner.setStoreName(request.getStoreName());

        return profileMapper.toResponse(ownerRepository.save(owner)); // <-- Cambia el nombre aquí también
    }
}