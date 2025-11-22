package com.example.catalogapi.communications.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuoteRequest(
        @Valid QuoteContact contact,
        @NotEmpty List<QuoteItem> items
) {
    public record QuoteContact(
            @NotBlank String name,
            @NotBlank @Email String email,
            String instructions
    ) {}

    public record QuoteItem(
            @NotBlank String name,
            @NotBlank String option,
            @NotNull Integer minQty,
            @NotNull Integer quantity,
            @NotNull Boolean available
    ) {}
}
