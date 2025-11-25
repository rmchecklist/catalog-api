package com.example.catalogapi.customer;

import java.util.List;

public record CustomerResponse(
        String code,
        CustomerType type,
        String primaryFirstName,
        String primaryLastName,
        String displayName,
        String company,
        String email,
        String phoneWork,
        String phoneMobile,
        AddressDto billingAddress,
        AddressDto shippingAddress,
        List<ContactPersonDto> contacts
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
