package com.example.catalogapi.product;

public record ProductSuggestion(
        String name,
        String brand,
        String category,
        String slug,
        String imageUrl
) { }
