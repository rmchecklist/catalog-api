package com.example.catalogapi.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotBlank String name,
        @NotBlank String option,
        @Min(1) int minQty,
        @Min(1) int quantity,
        @NotNull Boolean available
) { }
