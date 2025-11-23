package com.example.catalogapi.expense;

import jakarta.validation.constraints.NotBlank;

public record ExpenseCategoryRequest(
        @NotBlank String code,
        @NotBlank String name
) { }
