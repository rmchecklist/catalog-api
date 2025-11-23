package com.example.catalogapi.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.created(URI.create("/api/orders/" + response.id())).body(response);
    }

    @PostMapping("/quotes")
    public ResponseEntity<QuoteResponse> createQuote(@Valid @RequestBody OrderRequest request) {
        QuoteResponse response = orderService.createQuote(request);
        return ResponseEntity.created(URI.create("/api/quotes/" + response.id())).body(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        OrderResponse res = orderService.findOrder(id);
        if (res == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/quotes/{id}")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable UUID id) {
        QuoteResponse res = orderService.findQuote(id);
        if (res == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(res);
    }

}
