package com.example.catalogapi.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CustomerRequest(
        @NotBlank String code,
        @NotNull CustomerType type,
        String primaryFirstName,
        String primaryLastName,
        @NotBlank String displayName,
        String company,
        String email,
        String phoneWork,
        String phoneMobile,
        @Valid AddressDto billingAddress,
        @Valid AddressDto shippingAddress,
        @Valid List<ContactPersonDto> contacts
) {
    public record AddressDto(
            String attention,
            String address1,
            String address2,
            String city,
            String state,
            String zip,
            String phone
    ) {}

    public record ContactPersonDto(
            String firstName,
            String lastName,
            String email,
            String workPhone,
            String mobilePhone
    ) {}
}
