package com.intellimarket.api.profile.service;

import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.profile.dto.*;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import com.intellimarket.api.profile.repository.CustomerRepository;
import com.intellimarket.api.profile.repository.OwnerRepository;
import com.intellimarket.api.profile.mapper.ProfileMapper;
import com.intellimarket.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));

        // Opción B: si no existe el perfil, lo crea vacío automáticamente
        Customer customer = customerRepository.findById(user.getId())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder().user(user).build();
                    return customerRepository.save(newCustomer);
                });

        return profileMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public ProfileResponse updateCustomerProfileByEmail(String email, CustomerProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));

        Customer customer = customerRepository.findById(user.getId())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder().user(user).build();
                    return customerRepository.save(newCustomer);
                });

        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        return profileMapper.toResponse(customerRepository.save(customer));
    }

    // --- OWNER ---

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getOwnerProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));

        // Opción B: si no existe el perfil, lo crea vacío automáticamente
        Owner owner = ownerRepository.findById(user.getId())
                .orElseGet(() -> {
                    Owner newOwner = Owner.builder().user(user).build();
                    return ownerRepository.save(newOwner);
                });

        return profileMapper.toResponse(owner);
    }

    @Override
    @Transactional
    public ProfileResponse updateOwnerProfileByEmail(String email, OwnerProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));

        Owner owner = ownerRepository.findById(user.getId())
                .orElseGet(() -> {
                    Owner newOwner = Owner.builder().user(user).build();
                    return ownerRepository.save(newOwner);
                });

        owner.setPhone(request.phone());
        owner.setDni(request.dni());

        return profileMapper.toResponse(ownerRepository.save(owner));
    }
}