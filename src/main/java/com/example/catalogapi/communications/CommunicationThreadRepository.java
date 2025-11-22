package com.example.catalogapi.communications;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommunicationThreadRepository extends JpaRepository<CommunicationThreadEntity, UUID> {
}
