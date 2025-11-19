package com.example.catalogapi.product;

public record ProductOption(
        String label,
        String weight,
        Integer minQty,
        boolean available,
        String sku
) { }
