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

import com.intellimarket.api.shared.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    // --- CUSTOMER ---

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCustomerProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        Customer customer = customerRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado para el usuario: " + email));
        
        return profileMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public ProfileResponse updateCustomerProfileByEmail(String email, CustomerProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        Customer customer = customerRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado para el usuario: " + email));

        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        return profileMapper.toResponse(customerRepository.save(customer));
    }

    // --- OWNER ---

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getOwnerProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        Owner owner = ownerRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de dueño no encontrado para el usuario: " + email));
        
        return profileMapper.toResponse(owner);
    }

    @Override
    @Transactional
    public ProfileResponse updateOwnerProfileByEmail(String email, OwnerProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        Owner owner = ownerRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de dueño no encontrado para el usuario: " + email));

        owner.setPhone(request.phone());
        owner.setDni(request.dni());

        return profileMapper.toResponse(ownerRepository.save(owner));
    }
}