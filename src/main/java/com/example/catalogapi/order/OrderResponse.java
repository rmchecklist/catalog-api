package com.example.catalogapi.order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String invoiceNumber,
        String customerCode,
        String email,
        String name,
        String phone,
        String company,
        OrderStatus status,
        Instant createdAt,
        List<OrderItemDto> items,
        String pdfUrl
) { }

record OrderItemDto(
        String productSlug,
        String productName,
        String optionLabel,
        String sku,
        Integer quantity
) {}
