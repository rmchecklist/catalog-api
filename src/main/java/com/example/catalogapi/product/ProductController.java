package com.example.catalogapi.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> all() {
        return productService.findAll();
    }

    @GetMapping("/{slug}")
    public ProductResponse bySlug(@PathVariable String slug) {
        return productService.findBySlug(slug)
                .orElseThrow(() -> new ProductNotFoundException(slug));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
}
