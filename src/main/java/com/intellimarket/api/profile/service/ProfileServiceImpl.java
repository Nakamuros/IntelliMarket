package com.intellimarket.api.profile.service;

import com.intellimarket.api.profile.dto.CustomerProfileRequest;
import com.intellimarket.api.profile.dto.OwnerProfileRequest;
import com.intellimarket.api.profile.dto.ProfileResponse;
import com.intellimarket.api.profile.exception.ProfileNotFoundException;
import com.intellimarket.api.profile.mapper.ProfileMapper;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import com.intellimarket.api.profile.repository.CustomerRepository;
import com.intellimarket.api.profile.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements IProfileService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse getCustomerProfile(Long userId) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil de cliente no encontrado"));
        return profileMapper.toResponse(customer);
    }

    @Override
    public ProfileResponse updateCustomerProfile(Long userId, CustomerProfileRequest request) {
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil de cliente no encontrado"));

        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        Customer updatedCustomer = customerRepository.save(customer);
        return profileMapper.toResponse(updatedCustomer);
    }

    @Override
    public ProfileResponse getOwnerProfile(Long userId) {
        Owner owner = ownerRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil de dueño no encontrado"));
        return profileMapper.toResponse(owner);
    }

    @Override
    public ProfileResponse updateOwnerProfile(Long userId, OwnerProfileRequest request) {
        Owner owner = ownerRepository.findById(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Perfil de dueño no encontrado"));

        owner.setPhone(request.phone());
        owner.setDni(request.dni());

        Owner updatedOwner = ownerRepository.save(owner);
        return profileMapper.toResponse(updatedOwner);
    }
}