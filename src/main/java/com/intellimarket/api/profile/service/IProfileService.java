package com.intellimarket.api.profile.service;

import com.intellimarket.api.profile.dto.CustomerProfileRequest;
import com.intellimarket.api.profile.dto.OwnerProfileRequest;
import com.intellimarket.api.profile.dto.ProfileResponse;

public interface IProfileService {
    ProfileResponse getCustomerProfileByEmail(String email);
    ProfileResponse updateCustomerProfileByEmail(String email, CustomerProfileRequest request);

    ProfileResponse getOwnerProfileByEmail(String email);
    ProfileResponse updateOwnerProfileByEmail(String email, OwnerProfileRequest request);
}