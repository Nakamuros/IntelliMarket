package com.intellimarket.api.auth.mapper;

import com.intellimarket.api.auth.dto.RegisterRequest;
import com.intellimarket.api.auth.dto.AuthResponse;
import com.intellimarket.api.auth.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "CUSTOMER")
    User toUser(RegisterRequest request);

    @Mapping(target = "token", ignore = true)
    AuthResponse toAuthResponse(User user);
}