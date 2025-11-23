package com.example.catalogapi.order;

import java.time.Instant;
import java.util.UUID;

public record InvoiceSummaryResponse(
        UUID id,
        String invoiceNumber,
        String customerCode,
        String email,
        String type, // ORDER or QUOTE
        String status,
        Instant createdAt,
        String pdfUrl,
        String viewUrl
) { }
