package com.example.catalogapi.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.math.BigDecimal;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll(String search, String brand, String category) {
        List<ProductEntity> all = productRepository.findAll();
        String searchTerm = search == null ? "" : search.toLowerCase(Locale.ROOT);
        String brandTerm = brand == null ? "" : brand.toLowerCase(Locale.ROOT);
        String categoryTerm = category == null ? "" : category.toLowerCase(Locale.ROOT);

        return all.stream()
                .filter(p -> searchTerm.isEmpty() ||
                        p.getName().toLowerCase(Locale.ROOT).contains(searchTerm) ||
                        p.getDescription().toLowerCase(Locale.ROOT).contains(searchTerm))
                .filter(p -> brandTerm.isEmpty() || p.getBrand().toLowerCase(Locale.ROOT).equals(brandTerm))
                .filter(p -> categoryTerm.isEmpty() || p.getCategory().toLowerCase(Locale.ROOT).equals(categoryTerm))
                .map(this::toResponse)
                .toList();
    }

    public Optional<ProductResponse> findBySlug(String slug) {
        return productRepository.findBySlugIgnoreCase(slug).map(this::toResponse);
    }

    public ProductResponse create(ProductRequest request) {
        String slug = slugify(request.name());
        if (productRepository.existsBySlugIgnoreCase(slug)) {
            throw new IllegalArgumentException("Product already exists with slug: " + slug);
        }
        ProductEntity entity = mapToEntity(request, slug, null);
        ProductEntity saved = productRepository.save(entity);
        return toResponse(saved);
    }

    public ProductResponse update(String slug, ProductRequest request) {
        ProductEntity existing = productRepository.findBySlugIgnoreCase(slug)
                .orElseThrow(() -> new ProductNotFoundException(slug));
        // Keep existing slug to avoid breaking links; regenerate sku/option details.
        ProductEntity updated = mapToEntity(request, existing.getSlug(), existing);
        ProductEntity saved = productRepository.save(updated);
        return toResponse(saved);
    }

    public void delete(String slug) {
        ProductEntity existing = productRepository.findBySlugIgnoreCase(slug)
                .orElseThrow(() -> new ProductNotFoundException(slug));
        productRepository.delete(existing);
    }

    private ProductResponse toResponse(ProductEntity entity) {
        List<ProductOption> options = entity.getOptions().stream()
                .map(opt -> new ProductOption(
                        opt.getLabel(),
                        opt.getWeight(),
                        opt.getMinQty(),
                        opt.isAvailable(),
                        opt.getSku(),
                        opt.getPurchasePrice(),
                        opt.getSellingPrice(),
                        opt.getMarketPrice(),
                        opt.getStock() == null ? 0 : opt.getStock(),
                        opt.getLowStockThreshold() == null ? 10 : opt.getLowStockThreshold()))
                .toList();
        return new ProductResponse(
                entity.getName(),
                entity.getBrand(),
                entity.getVendor(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getSlug(),
                entity.getImageUrl(),
                options
        );
    }

    private ProductEntity mapToEntity(ProductRequest request, String slug, ProductEntity target) {
        ProductEntity entity = target == null ? new ProductEntity() : target;
        entity.setName(request.name());
        entity.setBrand(request.brand());
        entity.setVendor(request.vendor());
        entity.setCategory(request.category());
        entity.setDescription(request.description());
        entity.setSlug(slug);
        entity.setImageUrl(request.imageUrl());
        entity.getOptions().clear();
        entity.getOptions().addAll(request.options().stream().map(opt -> {
            ProductOptionEmbeddable emb = new ProductOptionEmbeddable();
            emb.setLabel(opt.label());
            emb.setWeight(opt.weight());
            emb.setMinQty(opt.minQty());
            emb.setAvailable(opt.available() == null || opt.available());
            emb.setSku(opt.sku() != null && !opt.sku().isBlank()
                    ? opt.sku().toUpperCase(Locale.ROOT)
                    : buildSku(request.vendor(), request.brand(), request.name(), opt.label()));
            emb.setPurchasePrice(defaultZero(opt.purchasePrice()));
            emb.setSellingPrice(defaultZero(opt.sellingPrice()));
            emb.setMarketPrice(opt.marketPrice());
            emb.setStock(opt.stock() == null ? 0 : opt.stock());
            emb.setLowStockThreshold(opt.lowStockThreshold() == null ? 10 : opt.lowStockThreshold());
            return emb;
        }).toList());
        return entity;
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }

    private String buildSku(String vendor, String brand, String name, String optionLabel) {
        String base = String.join("-", safe(vendor), safe(brand), safe(name), safe(optionLabel));
        return base.replaceAll("-+", "-").toUpperCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : slugify(value);
    }

    private BigDecimal defaultZero(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }
}
