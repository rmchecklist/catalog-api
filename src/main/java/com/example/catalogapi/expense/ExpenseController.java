package com.example.catalogapi.expense;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/expenses/" + saved.id())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ExpenseCategoryEntity>> categories() {
        return ResponseEntity.ok(service.categories());
    }

    @PostMapping("/categories")
    public ResponseEntity<ExpenseCategoryEntity> upsertCategory(@Valid @RequestBody ExpenseCategoryRequest request) {
        ExpenseCategoryEntity saved = service.upsertCategory(request);
        return ResponseEntity.created(URI.create("/api/admin/expenses/categories/" + saved.getCode())).body(saved);
    }

    @DeleteMapping("/categories/{code}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String code) {
        service.deleteCategory(code);
        return ResponseEntity.noContent().build();
    }
}
