package com.intellimarket.api.profile.service;

import com.intellimarket.api.profile.dto.CustomerProfileRequest;
import com.intellimarket.api.profile.dto.OwnerProfileRequest;
import com.intellimarket.api.profile.dto.ProfileResponse;

public interface IProfileService {
    ProfileResponse getCustomerProfile(Long userId);
    ProfileResponse updateCustomerProfile(Long userId, CustomerProfileRequest request);

    ProfileResponse getOwnerProfile(Long userId);
    ProfileResponse updateOwnerProfile(Long userId, OwnerProfileRequest request);
}
