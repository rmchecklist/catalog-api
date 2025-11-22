package com.example.catalogapi.order;

import com.example.catalogapi.communications.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final QuoteRepository quoteRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final MailService mailService;
    private final String externalBaseUrl;

    public OrderService(OrderRepository orderRepository, QuoteRepository quoteRepository, PdfGeneratorService pdfGeneratorService,
                        MailService mailService,
                        @Value("${app.external-base-url:http://localhost:8080}") String externalBaseUrl) {
        this.orderRepository = orderRepository;
        this.quoteRepository = quoteRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.mailService = mailService;
        this.externalBaseUrl = externalBaseUrl.endsWith("/") ? externalBaseUrl.substring(0, externalBaseUrl.length() - 1) : externalBaseUrl;
    }

    public OrderResponse createOrder(OrderRequest request) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerEmail(request.email());
        entity.setCustomerName(request.name());
        entity.setPhone(request.phone());
        entity.setCompany(request.company());
        entity.setStatus(OrderStatus.PENDING);
        entity.getItems().addAll(mapItems(request.items()));
        OrderEntity saved = orderRepository.save(entity);
        OrderResponse response = toResponse(saved);
        String viewLink = externalBaseUrl + "/api/orders/" + response.id();
        PdfRenderResult pdf = pdfGeneratorService.renderAndStoreOrderPdf(response, viewLink);
        OrderResponse enriched = new OrderResponse(response.id(), response.email(), response.name(), response.phone(), response.company(),
                response.status(), response.createdAt(), response.items(), pdf.publicUrl());
        sendOrderEmail(enriched, viewLink);
        return enriched;
    }

    public QuoteResponse createQuote(OrderRequest request) {
        QuoteEntity entity = new QuoteEntity();
        entity.setRequesterEmail(request.email());
        entity.setRequesterName(request.name());
        entity.setPhone(request.phone());
        entity.setCompany(request.company());
        entity.setStatus(QuoteStatus.RECEIVED);
        entity.getItems().addAll(mapItems(request.items()));
        QuoteEntity saved = quoteRepository.save(entity);
        QuoteResponse response = toResponse(saved);
        String viewLink = externalBaseUrl + "/api/quotes/" + response.id();
        PdfRenderResult pdf = pdfGeneratorService.renderAndStoreQuotePdf(response, viewLink);
        QuoteResponse enriched = new QuoteResponse(response.id(), response.email(), response.name(), response.phone(), response.company(),
                response.status(), response.createdAt(), response.items(), pdf.publicUrl());
        sendQuoteEmail(enriched, viewLink);
        return enriched;
    }

    public OrderResponse findOrder(UUID id) {
        return orderRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public QuoteResponse findQuote(UUID id) {
        return quoteRepository.findById(id).map(this::toResponse).orElse(null);
    }

    private List<OrderItemEmbeddable> mapItems(List<OrderItemRequest> items) {
        return items.stream().map(req -> {
            OrderItemEmbeddable emb = new OrderItemEmbeddable();
            emb.setProductSlug(req.productSlug());
            emb.setProductName(req.productName());
            emb.setOptionLabel(req.optionLabel());
            emb.setSku(req.sku());
            emb.setQuantity(req.quantity());
            emb.setSellingPrice(req.sellingPrice());
            emb.setMarketPrice(req.marketPrice());
            return emb;
        }).toList();
    }

    private OrderResponse toResponse(OrderEntity entity) {
        List<OrderItemDto> items = entity.getItems().stream()
                .map(i -> new OrderItemDto(i.getProductSlug(), i.getProductName(), i.getOptionLabel(), i.getSku(), i.getQuantity()))
                .toList();
        return new OrderResponse(entity.getId(), entity.getCustomerEmail(), entity.getCustomerName(), entity.getPhone(),
                entity.getCompany(), entity.getStatus(), entity.getCreatedAt(), items, null);
    }

    private QuoteResponse toResponse(QuoteEntity entity) {
        List<OrderItemDto> items = entity.getItems().stream()
                .map(i -> new OrderItemDto(i.getProductSlug(), i.getProductName(), i.getOptionLabel(), i.getSku(), i.getQuantity()))
                .toList();
        return new QuoteResponse(entity.getId(), entity.getRequesterEmail(), entity.getRequesterName(), entity.getPhone(),
                entity.getCompany(), entity.getStatus(), entity.getCreatedAt(), items, null);
    }

    private void sendOrderEmail(OrderResponse order, String viewLink) {
        if (order.email() == null || order.email().isBlank()) return;
        String subject = "Your order " + order.id();
        String body = """
                Thank you for your order.

                View online: %s
                PDF: %s
                """.formatted(viewLink, order.pdfUrl() == null ? viewLink : order.pdfUrl());
        mailService.sendText(order.email(), subject, body, null);
    }

    private void sendQuoteEmail(QuoteResponse quote, String viewLink) {
        if (quote.email() == null || quote.email().isBlank()) return;
        String subject = "Your quote request " + quote.id();
        String body = """
                Thanks for your request. We will respond shortly.

                View online: %s
                PDF: %s
                """.formatted(viewLink, quote.pdfUrl() == null ? viewLink : quote.pdfUrl());
        mailService.sendText(quote.email(), subject, body, null);
    }
}
