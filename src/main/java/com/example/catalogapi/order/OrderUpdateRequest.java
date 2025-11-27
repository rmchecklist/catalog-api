package com.example.catalogapi.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OrderUpdateRequest(
        @NotNull String status,
        @Valid List<ItemUpdate> items
) {
    public record ItemUpdate(
            String productSlug,
            String productName,
            String optionLabel,
            String sku,
            Integer quantity,
            BigDecimal sellingPrice,
            BigDecimal marketPrice
    ) { }
}
