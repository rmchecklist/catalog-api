package com.example.catalogapi.order;

import com.example.catalogapi.communications.MailService;
import com.example.catalogapi.customer.CustomerEntity;
import com.example.catalogapi.customer.CustomerRepository;
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
    private final String uiBaseUrl;
    private final InvoiceNumberService invoiceNumberService;
    private final CustomerRepository customerRepository;
    private final InvoiceSummaryBuilder invoiceSummaryBuilder;

    public OrderService(OrderRepository orderRepository, QuoteRepository quoteRepository, PdfGeneratorService pdfGeneratorService,
                        MailService mailService,
                        InvoiceNumberService invoiceNumberService,
                        CustomerRepository customerRepository,
                        InvoiceSummaryBuilder invoiceSummaryBuilder,
                        @Value("${app.external-base-url:http://localhost:8080/catalog}") String externalBaseUrl,
                        @Value("${app.ui-base-url:http://localhost:4200}") String uiBaseUrl) {
        this.orderRepository = orderRepository;
        this.quoteRepository = quoteRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.mailService = mailService;
        this.externalBaseUrl = externalBaseUrl.endsWith("/") ? externalBaseUrl.substring(0, externalBaseUrl.length() - 1) : externalBaseUrl;
        this.uiBaseUrl = uiBaseUrl.endsWith("/") ? uiBaseUrl.substring(0, uiBaseUrl.length() - 1) : uiBaseUrl;
        this.invoiceNumberService = invoiceNumberService;
        this.customerRepository = customerRepository;
        this.invoiceSummaryBuilder = invoiceSummaryBuilder;
    }

    public OrderResponse createOrder(OrderRequest request) {
        CustomerEntity customer = customerRepository.findById(request.customerCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown customer code " + request.customerCode()));
        OrderEntity entity = new OrderEntity();
        entity.setCustomerCode(request.customerCode());
        entity.setCustomerEmail(request.email());
        entity.setCustomerName(request.name());
        entity.setPhone(request.phone());
        entity.setCompany(request.company());
        entity.setStatus(OrderStatus.PENDING);
        entity.getItems().addAll(mapItems(request.items()));
        String invoiceNumber = invoiceNumberService.nextInvoiceNumber(request.customerCode());
        entity.setInvoiceNumber(invoiceNumber);
        OrderEntity saved = orderRepository.save(entity);
        OrderResponse response = toResponse(saved);
        String viewLink = uiBaseUrl + "/invoice/" + response.id();
        PdfRenderResult pdf = pdfGeneratorService.renderAndStoreOrderPdf(response, viewLink);
        saved.setPdfUrl(pdf.publicUrl());
        orderRepository.save(saved);
        OrderResponse enriched = new OrderResponse(response.id(), response.invoiceNumber(), response.customerCode(), response.email(), response.name(), response.phone(), response.company(),
                response.status(), response.createdAt(), response.items(), pdf.publicUrl());
        sendOrderEmail(enriched, viewLink, customer);
        return enriched;
    }

    public QuoteResponse createQuote(OrderRequest request) {
        CustomerEntity customer = customerRepository.findById(request.customerCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown customer code " + request.customerCode()));
        QuoteEntity entity = new QuoteEntity();
        entity.setCustomerCode(request.customerCode());
        entity.setRequesterEmail(request.email());
        entity.setRequesterName(request.name());
        entity.setPhone(request.phone());
        entity.setCompany(request.company());
        entity.setStatus(QuoteStatus.RECEIVED);
        entity.getItems().addAll(mapItems(request.items()));
        String invoiceNumber = invoiceNumberService.nextInvoiceNumber(request.customerCode());
        entity.setInvoiceNumber(invoiceNumber);
        QuoteEntity saved = quoteRepository.save(entity);
        QuoteResponse response = toResponse(saved);
        String viewLink = uiBaseUrl + "/invoice/" + response.id();
        PdfRenderResult pdf = pdfGeneratorService.renderAndStoreQuotePdf(response, viewLink);
        saved.setPdfUrl(pdf.publicUrl());
        quoteRepository.save(saved);
        QuoteResponse enriched = new QuoteResponse(response.id(), response.invoiceNumber(), response.customerCode(), response.email(), response.name(), response.phone(), response.company(),
                response.status(), response.createdAt(), response.items(), pdf.publicUrl());
        sendQuoteEmail(enriched, viewLink, customer);
        return enriched;
    }

    public OrderResponse findOrder(UUID id) {
        return orderRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public QuoteResponse findQuote(UUID id) {
        return quoteRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public void deleteOrder(UUID id) {
        orderRepository.findById(id).ifPresent(orderRepository::delete);
    }

    public void deleteQuote(UUID id) {
        quoteRepository.findById(id).ifPresent(quoteRepository::delete);
    }

    public boolean resendOrderEmail(UUID id, String emailOverride) {
        OrderEntity entity = orderRepository.findById(id).orElse(null);
        if (entity == null) return false;
        OrderResponse response = toResponse(entity);
        String viewLink = uiBaseUrl + "/invoice/" + response.id();
        PdfRenderResult pdf = pdfGeneratorService.renderAndStoreOrderPdf(response, viewLink);
        if (entity != null) {
            entity.setPdfUrl(pdf.publicUrl());
            orderRepository.save(entity);
        }
        OrderResponse enriched = new OrderResponse(response.id(), response.invoiceNumber(), response.customerCode(), emailOverride, response.name(), response.phone(), response.company(),
                response.status(), response.createdAt(), response.items(), pdf.publicUrl());
        CustomerEntity customer = customerRepository.findById(response.customerCode()).orElse(null);
        sendOrderEmail(enriched, viewLink, customer);
        return true;
    }

    public boolean resendQuoteEmail(UUID id, String emailOverride) {
        QuoteEntity entity = quoteRepository.findById(id).orElse(null);
        if (entity == null) return false;
        QuoteResponse response = toResponse(entity);
        String viewLink = uiBaseUrl + "/invoice/" + response.id();
        PdfRenderResult pdf = pdfGeneratorService.renderAndStoreQuotePdf(response, viewLink);
        if (entity != null) {
            entity.setPdfUrl(pdf.publicUrl());
            quoteRepository.save(entity);
        }
        QuoteResponse enriched = new QuoteResponse(response.id(), response.invoiceNumber(), response.customerCode(), emailOverride, response.name(), response.phone(), response.company(),
                response.status(), response.createdAt(), response.items(), pdf.publicUrl());
        CustomerEntity customer = customerRepository.findById(response.customerCode()).orElse(null);
        sendQuoteEmail(enriched, viewLink, customer);
        return true;
    }

    public List<InvoiceSummaryResponse> listInvoices() {
        List<InvoiceSummaryResponse> orders = orderRepository.findAll().stream()
                .map(invoiceSummaryBuilder::fromOrder)
                .toList();
        List<InvoiceSummaryResponse> quotes = quoteRepository.findAll().stream()
                .map(invoiceSummaryBuilder::fromQuote)
                .toList();
        return new java.util.ArrayList<InvoiceSummaryResponse>() {{
            addAll(orders);
            addAll(quotes);
        }}.stream()
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                .toList();
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
        return new OrderResponse(entity.getId(), entity.getInvoiceNumber(), entity.getCustomerCode(), entity.getCustomerEmail(), entity.getCustomerName(), entity.getPhone(),
                entity.getCompany(), entity.getStatus(), entity.getCreatedAt(), items, entity.getPdfUrl());
    }

    private QuoteResponse toResponse(QuoteEntity entity) {
        List<OrderItemDto> items = entity.getItems().stream()
                .map(i -> new OrderItemDto(i.getProductSlug(), i.getProductName(), i.getOptionLabel(), i.getSku(), i.getQuantity()))
                .toList();
        return new QuoteResponse(entity.getId(), entity.getInvoiceNumber(), entity.getCustomerCode(), entity.getRequesterEmail(), entity.getRequesterName(), entity.getPhone(),
                entity.getCompany(), entity.getStatus(), entity.getCreatedAt(), items, entity.getPdfUrl());
    }

    private void sendOrderEmail(OrderResponse order, String viewLink, CustomerEntity customer) {
        if (order.email() == null || order.email().isBlank()) return;
        String subject = "Ilan Foods · Order " + order.invoiceNumber();
        String html = emailTemplate("Order", order.invoiceNumber(), customer.getDisplayName(), viewLink, order.pdfUrl(), "Thank you for your order.");
        mailService.sendHtml(order.email(), subject, html, null);
    }

    private void sendQuoteEmail(QuoteResponse quote, String viewLink, CustomerEntity customer) {
        if (quote.email() == null || quote.email().isBlank()) return;
        String subject = "Ilan Foods · Quote " + quote.invoiceNumber();
        String html = emailTemplate("Quote", quote.invoiceNumber(), customer.getDisplayName(), viewLink, quote.pdfUrl(), "Thanks for your request. We will respond shortly.");
        mailService.sendHtml(quote.email(), subject, html, null);
    }

    private String emailTemplate(String kind, String invoiceNumber, String customerName, String viewLink, String pdfLink, String message) {
        String pdf = pdfLink == null ? viewLink : pdfLink;
        return """
                <div style="font-family:Arial,sans-serif;color:#0f172a">
                  <h2>Ilan Foods</h2>
                  <p>%s</p>
                  <p><strong>%s:</strong> %s</p>
                  <p><strong>Customer:</strong> %s</p>
                  <p><a href="%s">View online</a> &nbsp;|&nbsp; <a href="%s">Download PDF</a></p>
                  <hr/>
                  <p style="font-size:12px;color:#475569;">445 Hawks Creek Pkwy, Fort Mill SC · catalog.ilanfoods.com · 717-215-0206</p>
                </div>
                """.formatted(message, kind, invoiceNumber, customerName, viewLink, pdf);
    }
}
