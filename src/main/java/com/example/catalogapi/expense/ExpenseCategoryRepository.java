package com.example.catalogapi.expense;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategoryEntity, String> {
}
