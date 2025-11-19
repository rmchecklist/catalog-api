package com.example.catalogapi.cart;

public record CartItem(
        String id,
        String name,
        String option,
        int minQty,
        int quantity,
        boolean available
) { }
