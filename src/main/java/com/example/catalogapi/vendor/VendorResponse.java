package com.example.catalogapi.vendor;

public record VendorResponse(
        String code,
        String name,
        String email,
        String phone,
        String company,
        String address
) { }
