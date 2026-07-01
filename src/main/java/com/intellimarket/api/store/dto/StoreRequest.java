package com.intellimarket.api.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRequest(
    @NotBlank(message = "El nombre de la tienda es obligatorio")
    @Size(max = 30, message = "El nombre no puede exceder los 30 caracteres")
    String name,

    @NotBlank(message = "La dirección es obligatoria")
    String address,

    @NotBlank(message = "El distrito es obligatorio")
    @Size(max = 50, message = "El distrito no puede exceder los 50 caracteres")
    String district,

    String imageUrl
) {}
