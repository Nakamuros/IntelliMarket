package com.intellimarket.api.profile.mapper;

import com.intellimarket.api.profile.dto.ProfileResponse;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(Customer customer) {
        if (customer == null) return null;
        
        Long userId = null;
        String firstName = null;
        String lastName = null;
        String email = null;

        if (customer.getUser() != null) {
            userId = customer.getUser().getId();
            firstName = customer.getUser().getFirstName();
            lastName = customer.getUser().getLastName();
            email = customer.getUser().getEmail();
        }

        return new ProfileResponse(
                userId,
                firstName,
                lastName,
                email,
                customer.getPhone(),
                customer.getAddress(),
                null
        );
    }

    public ProfileResponse toResponse(Owner owner) {
        if (owner == null) return null;

        Long userId = null;
        String firstName = null;
        String lastName = null;
        String email = null;

        if (owner.getUser() != null) {
            userId = owner.getUser().getId();
            firstName = owner.getUser().getFirstName();
            lastName = owner.getUser().getLastName();
            email = owner.getUser().getEmail();
        }

        return new ProfileResponse(
                userId,
                firstName,
                lastName,
                email,
                owner.getPhone(),
                null,
                owner.getDni()
        );
    }
}