package com.example.catalogapi.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String brand,
        String vendor,
        @NotBlank String category,
        @NotBlank String description,
        String imageUrl,
        @NotEmpty List<ProductOptionRequest> options
) { }

record ProductOptionRequest(
        @NotBlank String label,
        String weight,
        @NotNull Integer minQty,
        Boolean available,
        java.math.BigDecimal purchasePrice,
        java.math.BigDecimal sellingPrice,
        java.math.BigDecimal marketPrice,
        String sku,
        Integer stock,
        Integer lowStockThreshold
) {}
