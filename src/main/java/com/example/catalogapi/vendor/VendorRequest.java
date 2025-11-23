package com.example.catalogapi.vendor;

import jakarta.validation.constraints.NotBlank;

public record VendorRequest(
        @NotBlank String code,
        @NotBlank String name,
        String email,
        String phone,
        String company,
        String address
) { }
