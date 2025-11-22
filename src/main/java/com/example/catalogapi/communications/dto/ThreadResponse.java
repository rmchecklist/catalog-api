package com.example.catalogapi.communications.dto;

import com.example.catalogapi.communications.CommunicationThreadStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ThreadResponse(
        UUID id,
        String subject,
        CommunicationThreadStatus status,
        OffsetDateTime updatedAt,
        List<MessageSummary> messages
) {
    public record MessageSummary(
            UUID id,
            String from,
            String to,
            String body,
            String direction,
            OffsetDateTime createdAt
    ) {}
}
