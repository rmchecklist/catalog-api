package com.example.catalogapi.product;

import java.util.List;

public record ProductResponse(
        String name,
        String brand,
        String vendor,
        String category,
        String description,
        String slug,
        String imageUrl,
        List<ProductOption> options
) { }
