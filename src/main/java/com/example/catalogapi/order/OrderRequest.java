package com.example.catalogapi.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        @Email @NotBlank String email,
        String name,
        String phone,
        String company,
        @NotEmpty List<OrderItemRequest> items
) { }

record OrderItemRequest(
        @NotBlank String productSlug,
        @NotBlank String productName,
        @NotBlank String optionLabel,
        String sku,
        @NotNull Integer quantity,
        BigDecimal sellingPrice,
        BigDecimal marketPrice
) {}
