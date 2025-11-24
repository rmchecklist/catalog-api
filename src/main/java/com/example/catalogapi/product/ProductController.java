package com.example.catalogapi.product;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> all(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "category", required = false) String category
    ) {
        return productService.findAll(search, brand, category);
    }

    @GetMapping("/search")
    public List<ProductSuggestion> search(
            @RequestParam("term") String term,
            @RequestParam(value = "limit", defaultValue = "8") int limit
    ) {
        return productService.searchSuggestions(term, Math.max(1, Math.min(limit, 20)));
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

    @PutMapping("/{slug}")
    public ProductResponse update(@PathVariable String slug, @Valid @RequestBody ProductRequest request) {
        return productService.update(slug, request);
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String slug) {
        productService.delete(slug);
    }
}
