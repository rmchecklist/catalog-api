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
        String pdfUrl,
        java.math.BigDecimal totalAmount
) { }

record OrderItemDto(
        String productSlug,
        String productName,
        String optionLabel,
        String sku,
        Integer quantity,
        java.math.BigDecimal sellingPrice,
        java.math.BigDecimal marketPrice,
        java.math.BigDecimal lineTotal
) {}
