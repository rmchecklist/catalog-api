package com.example.catalogapi.cart;

import jakarta.validation.constraints.Min;

public record CartQuantityRequest(@Min(1) int quantity) { }
