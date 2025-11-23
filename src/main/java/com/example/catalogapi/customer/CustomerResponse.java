package com.example.catalogapi.customer;

public record CustomerResponse(
        String code,
        String name,
        String email,
        String phone,
        String company,
        String address
) { }
