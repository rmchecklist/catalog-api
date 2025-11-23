package com.example.catalogapi.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank String category,
        @NotBlank String description,
        @NotNull BigDecimal amount,
        @NotBlank String currency,
        @NotNull LocalDate date,
        String vendor,
        String receiptUrl,
        String notes
) { }
