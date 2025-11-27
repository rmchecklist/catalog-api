package com.example.catalogapi.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/invoices")
public class InvoiceAdminController {

    private final OrderService orderService;

    public InvoiceAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceSummaryResponse>> list() {
        return ResponseEntity.ok(orderService.listInvoices());
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable java.util.UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/quotes/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable java.util.UUID id) {
        orderService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders/{id}/history")
    public ResponseEntity<List<StatusHistoryResponse>> orderHistory(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(orderService.getHistory(id, StatusHistoryEntity.ParentType.ORDER));
    }

    @GetMapping("/quotes/{id}/history")
    public ResponseEntity<List<StatusHistoryResponse>> quoteHistory(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(orderService.getHistory(id, StatusHistoryEntity.ParentType.QUOTE));
    }

    @PostMapping("/orders/{id}/send")
    public ResponseEntity<Void> resendOrder(@PathVariable java.util.UUID id, @RequestBody TargetEmailRequest target) {
        boolean ok = orderService.resendOrderEmail(id, target.email());
        return ok ? ResponseEntity.accepted().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/quotes/{id}/send")
    public ResponseEntity<Void> resendQuote(@PathVariable java.util.UUID id, @RequestBody TargetEmailRequest target) {
        boolean ok = orderService.resendQuoteEmail(id, target.email());
        return ok ? ResponseEntity.accepted().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Void> updateOrder(@PathVariable java.util.UUID id, @Valid @RequestBody OrderUpdateRequest request) {
        boolean ok = orderService.updateOrder(id, request);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.status(409).build();
    }

    @PutMapping("/quotes/{id}")
    public ResponseEntity<Void> updateQuote(@PathVariable java.util.UUID id, @Valid @RequestBody OrderUpdateRequest request) {
        boolean ok = orderService.updateQuote(id, request);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.status(409).build();
    }
}
