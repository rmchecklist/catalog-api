package com.example.catalogapi.cart;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {

    private final Map<String, CartItem> items = new ConcurrentHashMap<>();

    public CartService() {
        seed();
    }

    public List<CartItem> findAll() {
        return new ArrayList<>(items.values());
    }

    public CartItem add(CartItemRequest request) {
        String id = UUID.randomUUID().toString();
        CartItem item = new CartItem(
                id,
                request.name(),
                request.option(),
                request.minQty(),
                Math.max(request.minQty(), request.quantity()),
                request.available()
        );
        items.put(id, item);
        return item;
    }

    public CartItem updateQuantity(String id, CartQuantityRequest request) {
        CartItem existing = items.get(id);
        if (existing == null) {
            throw new CartItemNotFoundException(id);
        }
        int qty = Math.max(existing.minQty(), request.quantity());
        CartItem updated = new CartItem(
                existing.id(),
                existing.name(),
                existing.option(),
                existing.minQty(),
                qty,
                existing.available()
        );
        items.put(id, updated);
        return updated;
    }

    public void remove(String id) {
        if (items.remove(id) == null) {
            throw new CartItemNotFoundException(id);
        }
    }

    private void seed() {
        add(new CartItemRequest("Stainless Bottle", "750ml", 25, 25, true));
        add(new CartItemRequest("Eco Mailer", "M", 100, 200, true));
        add(new CartItemRequest("Carbon Steel Hex Bolt", "M10 x 50", 50, 50, false));
    }
}
