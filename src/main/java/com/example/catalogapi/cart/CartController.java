package com.example.catalogapi.cart;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartItem> list() {
        return cartService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItem add(@Valid @RequestBody CartItemRequest request) {
        return cartService.add(request);
    }

    @PatchMapping("/{id}")
    public CartItem updateQuantity(@PathVariable String id, @Valid @RequestBody CartQuantityRequest request) {
        return cartService.updateQuantity(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String id) {
        cartService.remove(id);
    }
}
