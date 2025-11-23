package com.example.catalogapi.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceCounterRepository extends JpaRepository<InvoiceCounterEntity, String> {
}
