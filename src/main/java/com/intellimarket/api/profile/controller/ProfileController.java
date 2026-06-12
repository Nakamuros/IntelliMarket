package com.intellimarket.api.profile.controller;

import com.intellimarket.api.profile.dto.CustomerProfileRequest;
import com.intellimarket.api.profile.dto.OwnerProfileRequest;
import com.intellimarket.api.profile.dto.ProfileResponse;
import com.intellimarket.api.profile.service.IProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "https://localhost:4200")
@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    // --- Endpoints para Customers ---
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/me")
    public ResponseEntity<ProfileResponse> getMyCustomerProfile(Authentication authentication) {
        String email = authentication.getName(); // Extrae el correo del token JWT
        return ResponseEntity.ok(profileService.getCustomerProfileByEmail(email));
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/customer/me")
    public ResponseEntity<ProfileResponse> updateMyCustomerProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateCustomerProfileByEmail(email, request));
    }

    // --- Endpoints para Owners ---
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/owner/me")
    public ResponseEntity<ProfileResponse> getMyOwnerProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.getOwnerProfileByEmail(email));
    }
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/owner/me")
    public ResponseEntity<ProfileResponse> updateMyOwnerProfile(
            Authentication authentication,
            @Valid @RequestBody OwnerProfileRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(profileService.updateOwnerProfileByEmail(email, request));
    }
}