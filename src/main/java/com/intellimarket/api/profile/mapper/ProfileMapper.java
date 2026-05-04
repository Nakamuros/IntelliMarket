package com.intellimarket.api.profile.mapper;

import com.intellimarket.api.profile.dto.ProfileResponse;
import com.intellimarket.api.profile.model.Customer;
import com.intellimarket.api.profile.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(Customer customer) {
        return new ProfileResponse(
                customer.getUser().getId(),
                customer.getUser().getFirstName(),
                customer.getUser().getLastName(),
                customer.getUser().getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                null
        );
    }

    public ProfileResponse toResponse(Owner owner) {
        return new ProfileResponse(
                owner.getUser().getId(),
                owner.getUser().getFirstName(),
                owner.getUser().getLastName(),
                owner.getUser().getEmail(),
                owner.getPhone(),
                null,
                owner.getDni()

        );
    }
}