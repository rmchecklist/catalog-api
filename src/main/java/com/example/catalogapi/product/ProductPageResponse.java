package com.example.catalogapi.product;

import java.util.List;

public record ProductPageResponse(
        List<ProductResponse> items,
        long total,
        int page,
        int size
) { }
