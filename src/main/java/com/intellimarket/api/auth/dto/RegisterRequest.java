package com.intellimarket.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Size(max = 30) @NotBlank (message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Size(max = 30) @NotBlank(message = "First name is required")
    private String firstName;

    @Size(max = 30) @NotBlank(message = "Last name is required")
    private String lastName;

    private com.intellimarket.api.auth.model.Role role;
}

