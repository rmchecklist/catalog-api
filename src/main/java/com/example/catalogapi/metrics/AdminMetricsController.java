package com.example.catalogapi.metrics;

import com.example.catalogapi.customer.CustomerRepository;
import com.example.catalogapi.order.OrderEntity;
import com.example.catalogapi.order.OrderRepository;
import com.example.catalogapi.order.OrderStatus;
import com.example.catalogapi.order.QuoteEntity;
import com.example.catalogapi.order.QuoteRepository;
import com.example.catalogapi.order.QuoteStatus;
import com.example.catalogapi.product.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/metrics")
public class AdminMetricsController {

    private final OrderRepository orderRepository;
    private final QuoteRepository quoteRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public AdminMetricsController(OrderRepository orderRepository,
                                  QuoteRepository quoteRepository,
                                  ProductRepository productRepository,
                                  CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.quoteRepository = quoteRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/orders")
    public OrderMetrics orders() {
        List<OrderEntity> orders = orderRepository.findAll();
        long total = orders.size();
        long open = orders.stream().filter(o -> !isFinal(o.getStatus())).count();
        long fulfilled = orders.stream().filter(o -> o.getStatus() == OrderStatus.FULFILLED).count();
        long shipped = orders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count();
        BigDecimal revenue = orders.stream()
                .map(OrderEntity::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Long> byStatus = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), Collectors.counting()));
        Map<String, BigDecimal> byMonth = orders.stream().collect(Collectors.groupingBy(
                o -> o.getCreatedAt() != null ? o.getCreatedAt().toString().substring(0, 7) : "unknown",
                Collectors.mapping(OrderEntity::getTotalAmount,
                        Collectors.reducing(BigDecimal.ZERO, e -> e == null ? BigDecimal.ZERO : e, BigDecimal::add))
        ));
        return new OrderMetrics(total, open, fulfilled, shipped, revenue, byStatus, byMonth);
    }

    @GetMapping("/quotes")
    public QuoteMetrics quotes() {
        List<QuoteEntity> quotes = quoteRepository.findAll();
        long total = quotes.size();
        long open = quotes.stream().filter(q -> !isFinal(q.getStatus())).count();
        long responded = quotes.stream().filter(q -> q.getStatus() == QuoteStatus.RESPONDED).count();
        long approved = quotes.stream().filter(q -> q.getStatus() == QuoteStatus.APPROVED).count();
        Map<String, Long> byStatus = quotes.stream()
                .collect(Collectors.groupingBy(q -> q.getStatus().name(), Collectors.counting()));
        Map<String, Long> byMonth = quotes.stream().collect(Collectors.groupingBy(
                q -> q.getCreatedAt() != null ? q.getCreatedAt().toString().substring(0, 7) : "unknown",
                Collectors.counting()
        ));
        return new QuoteMetrics(total, open, responded, approved, byStatus, byMonth);
    }

    @GetMapping("/products")
    public ProductMetrics products() {
        long total = productRepository.count();
        return new ProductMetrics(total);
    }

    @GetMapping("/customers")
    public CustomerMetrics customers() {
        long total = customerRepository.count();
        return new CustomerMetrics(total);
    }

    private boolean isFinal(OrderStatus status) {
        return status == OrderStatus.SHIPPED || status == OrderStatus.CLOSED || status == OrderStatus.CANCELLED;
    }

    private boolean isFinal(QuoteStatus status) {
        return status == QuoteStatus.SHIPPED || status == QuoteStatus.CLOSED || status == QuoteStatus.CANCELLED;
    }

    public record OrderMetrics(long total, long open, long fulfilled, long shipped, BigDecimal revenue,
                               Map<String, Long> byStatus,
                               Map<String, BigDecimal> revenueByMonth) { }
    public record QuoteMetrics(long total, long open, long responded, long approved,
                               Map<String, Long> byStatus,
                               Map<String, Long> byMonth) { }
    public record ProductMetrics(long total) { }
    public record CustomerMetrics(long total) { }
}
