package com.intellimarket.api.profile.dto;

public record ProfileResponse(
        Long userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address, //customer
        String dni //owner
) {}
