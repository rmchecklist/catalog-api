package com.example.catalogapi.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InvoiceSummaryBuilder {

    private final String externalBaseUrl;

    public InvoiceSummaryBuilder(@Value("${app.external-base-url:http://localhost:8080/catalog}") String externalBaseUrl) {
        this.externalBaseUrl = externalBaseUrl.endsWith("/") ? externalBaseUrl.substring(0, externalBaseUrl.length() - 1) : externalBaseUrl;
    }

    public InvoiceSummaryResponse fromOrder(OrderEntity entity) {
        String view = externalBaseUrl + "/api/orders/" + entity.getId();
        return new InvoiceSummaryResponse(
                entity.getId(),
                entity.getInvoiceNumber(),
                entity.getCustomerCode(),
                entity.getCustomerEmail(),
                "ORDER",
                entity.getStatus().name(),
                entity.getCreatedAt(),
                null,
                view,
                entity.getTotalAmount()
        );
    }

    public InvoiceSummaryResponse fromQuote(QuoteEntity entity) {
        String view = externalBaseUrl + "/api/quotes/" + entity.getId();
        return new InvoiceSummaryResponse(
                entity.getId(),
                entity.getInvoiceNumber(),
                entity.getCustomerCode(),
                entity.getRequesterEmail(),
                "QUOTE",
                entity.getStatus().name(),
                entity.getCreatedAt(),
                null,
                view,
                entity.getTotalAmount()
        );
    }
}
