package com.example.catalogapi.product;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductService {

    private final List<ProductResponse> inMemoryProducts = new ArrayList<>();

    public ProductService() {
        seed();
    }

    public List<ProductResponse> findAll(String search, String brand, String category) {
        if ((search == null || search.isBlank()) &&
                (brand == null || brand.isBlank()) &&
                (category == null || category.isBlank())) {
            return Collections.unmodifiableList(inMemoryProducts);
        }

        String searchTerm = search == null ? "" : search.toLowerCase(Locale.ROOT);
        String brandTerm = brand == null ? "" : brand.toLowerCase(Locale.ROOT);
        String categoryTerm = category == null ? "" : category.toLowerCase(Locale.ROOT);

        return inMemoryProducts.stream()
                .filter(p -> searchTerm.isEmpty() ||
                        p.name().toLowerCase(Locale.ROOT).contains(searchTerm) ||
                        p.description().toLowerCase(Locale.ROOT).contains(searchTerm))
                .filter(p -> brandTerm.isEmpty() || p.brand().toLowerCase(Locale.ROOT).equals(brandTerm))
                .filter(p -> categoryTerm.isEmpty() || p.category().toLowerCase(Locale.ROOT).equals(categoryTerm))
                .toList();
    }

    public Optional<ProductResponse> findBySlug(String slug) {
        return inMemoryProducts.stream()
                .filter(p -> p.slug().equalsIgnoreCase(slug))
                .findFirst();
    }

    public ProductResponse create(ProductRequest request) {
        String slug = slugify(request.name());
        List<ProductOption> options = mapOptions(request);
        ProductResponse product = new ProductResponse(
                request.name(),
                request.brand(),
                request.vendor(),
                request.category(),
                request.description(),
                slug,
                request.imageUrl(),
                options
        );
        inMemoryProducts.add(product);
        return product;
    }

    public ProductResponse update(String slug, ProductRequest request) {
        int index = findIndexBySlug(slug);
        List<ProductOption> options = mapOptions(request);
        ProductResponse updated = new ProductResponse(
                request.name(),
                request.brand(),
                request.vendor(),
                request.category(),
                request.description(),
                slug,
                request.imageUrl(),
                options
        );
        inMemoryProducts.set(index, updated);
        return updated;
    }

    public void delete(String slug) {
        boolean removed = inMemoryProducts.removeIf(p -> p.slug().equalsIgnoreCase(slug));
        if (!removed) {
            throw new ProductNotFoundException(slug);
        }
    }

    private int findIndexBySlug(String slug) {
        for (int i = 0; i < inMemoryProducts.size(); i++) {
            if (inMemoryProducts.get(i).slug().equalsIgnoreCase(slug)) {
                return i;
            }
        }
        throw new ProductNotFoundException(slug);
    }

    private List<ProductOption> mapOptions(ProductRequest request) {
        return request.options().stream()
                .map(opt -> new ProductOption(
                        opt.label(),
                        opt.weight(),
                        opt.minQty(),
                        opt.available() == null || opt.available(),
                        buildSku(request.vendor(), request.brand(), request.name(), opt.label())
                ))
                .toList();
    }

    private String buildSku(String vendor, String brand, String name, String optionLabel) {
        String base = String.join("-", safe(vendor), safe(brand), safe(name), safe(optionLabel));
        return base.replaceAll("-+", "-").toUpperCase(Locale.ROOT);
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }

    private String safe(String value) {
        return value == null ? "" : slugify(value);
    }

    private void seed() {
        create(new ProductRequest(
                "Stainless Bottle",
                "Acme",
                "Acme",
                "Beverages",
                "Insulated stainless steel bottle with multiple weight/size options.",
                "https://images.unsplash.com/photo-1526402462921-9fe5c5f2a2c9?auto=format&fit=crop&w=1200&q=80",
                List.of(
                        new ProductOptionRequest("500ml", "0.5kg", 25, true),
                        new ProductOptionRequest("750ml", "0.7kg", 25, true),
                        new ProductOptionRequest("1L", "1.0kg", 25, false)
                )
        ));

        create(new ProductRequest(
                "Eco Mailer",
                "Northwind",
                "Northwind",
                "Packaging",
                "Compostable mailer with reinforced seams, ships flat.",
                "https://images.unsplash.com/photo-1525909002-1b05e0c869d0?auto=format&fit=crop&w=1200&q=80",
                List.of(
                        new ProductOptionRequest("Small", null, 100, true),
                        new ProductOptionRequest("Medium", null, 100, true),
                        new ProductOptionRequest("Large", null, 100, true)
                )
        ));

        create(new ProductRequest(
                "Carbon Steel Hex Bolt",
                "Globex",
                "Globex",
                "Hardware",
                "Industrial grade hex bolt, metric and imperial options.",
                "https://images.unsplash.com/photo-1503389152951-9f343605f61e?auto=format&fit=crop&w=1200&q=80",
                List.of(
                        new ProductOptionRequest("M8 x 40", null, 50, true),
                        new ProductOptionRequest("M10 x 50", null, 50, true),
                        new ProductOptionRequest("3/8in x 2in", null, 50, false)
                )
        ));
    }
}
