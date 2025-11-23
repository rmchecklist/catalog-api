package com.example.catalogapi.customer;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String code,
        @NotBlank String name,
        String email,
        String phone,
        String company,
        String address
) { }
