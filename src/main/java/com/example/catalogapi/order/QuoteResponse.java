package com.example.catalogapi.order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record QuoteResponse(
        UUID id,
        String email,
        String name,
        String phone,
        String company,
        QuoteStatus status,
        Instant createdAt,
        List<OrderItemDto> items,
        String pdfUrl
) { }
