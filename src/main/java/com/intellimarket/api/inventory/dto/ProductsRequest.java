package com.intellimarket.api.inventory.dto;

import com.intellimarket.api.inventory.model.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductsRequest (
        @NotBlank
        @Size(max = 60)
        String name,

        @NotNull
        Category category,

        @NotBlank
        @Size(max = 130)
        String description,

        @NotNull
        @DecimalMin(value = "0.10", message = "El precio no debe ser menor a 10 céntimos")
        @DecimalMax(value = "200.00", message = "El precio no puede superar 200 soles")
        BigDecimal unitPrice,

        @NotNull
        @Min(value = 0, message = "El stock inicial no puede ser negativo")
        Integer stock

        //@NotNull
        //Long user_id,

        //@NotNull
        //Stores store_id
        //Long store_id
        //Long id
) {}