package com.example.catalogapi.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        String category,
        String description,
        BigDecimal amount,
        String currency,
        LocalDate date,
        String vendor,
        String receiptUrl,
        String notes
) { }
