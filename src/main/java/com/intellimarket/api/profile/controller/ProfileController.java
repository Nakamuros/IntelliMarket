package com.intellimarket.api.profile.controller;

import com.intellimarket.api.profile.dto.CustomerProfileRequest;
import com.intellimarket.api.profile.dto.OwnerProfileRequest;
import com.intellimarket.api.profile.dto.ProfileResponse;
import com.intellimarket.api.profile.service.IProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    // --- Endpoints para Customers ---

    @GetMapping("/customer/{userId}")
    public ResponseEntity<ProfileResponse> getCustomerProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getCustomerProfile(userId));
    }

    @PutMapping("/customer/{userId}")
    public ResponseEntity<ProfileResponse> updateCustomerProfile(
            @PathVariable Long userId,
            @Valid @RequestBody CustomerProfileRequest request) {
        return ResponseEntity.ok(profileService.updateCustomerProfile(userId, request));
    }

    // --- Endpoints para Owners ---

    @GetMapping("/owner/{userId}")
    public ResponseEntity<ProfileResponse> getOwnerProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getOwnerProfile(userId));
    }

    @PutMapping("/owner/{userId}")
    public ResponseEntity<ProfileResponse> updateOwnerProfile(
            @PathVariable Long userId,
            @Valid @RequestBody OwnerProfileRequest request) {
        return ResponseEntity.ok(profileService.updateOwnerProfile(userId, request));
    }
}