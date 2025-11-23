package com.example.catalogapi.expense;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseCategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ExpenseResponse> findAll() {
        return expenseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ExpenseResponse create(ExpenseRequest request) {
        ExpenseEntity entity = new ExpenseEntity();
        apply(entity, request);
        return toResponse(expenseRepository.save(entity));
    }

    public ExpenseResponse update(UUID id, ExpenseRequest request) {
        ExpenseEntity entity = expenseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));
        apply(entity, request);
        return toResponse(expenseRepository.save(entity));
    }

    public void delete(UUID id) {
        expenseRepository.findById(id).ifPresent(expenseRepository::delete);
    }

    public List<ExpenseCategoryEntity> categories() {
        return categoryRepository.findAll();
    }

    public ExpenseCategoryEntity upsertCategory(ExpenseCategoryRequest request) {
        ExpenseCategoryEntity cat = categoryRepository.findById(request.code()).orElseGet(ExpenseCategoryEntity::new);
        cat.setCode(request.code());
        cat.setName(request.name());
        return categoryRepository.save(cat);
    }

    public void deleteCategory(String code) {
        categoryRepository.findById(code).ifPresent(categoryRepository::delete);
    }

    private void apply(ExpenseEntity entity, ExpenseRequest request) {
        entity.setCategory(request.category());
        entity.setDescription(request.description());
        entity.setAmount(request.amount());
        entity.setCurrency(request.currency());
        entity.setDate(request.date());
        entity.setVendor(request.vendor());
        entity.setReceiptUrl(request.receiptUrl());
        entity.setNotes(request.notes());
    }

    private ExpenseResponse toResponse(ExpenseEntity entity) {
        return new ExpenseResponse(
                entity.getId(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDate(),
                entity.getVendor(),
                entity.getReceiptUrl(),
                entity.getNotes()
        );
    }
}
