package com.example.catalogapi.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StatusHistoryRepository extends JpaRepository<StatusHistoryEntity, UUID> {
}
