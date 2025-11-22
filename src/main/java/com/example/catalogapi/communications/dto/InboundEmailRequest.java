package com.example.catalogapi.communications.dto;

import jakarta.validation.constraints.NotBlank;

public record InboundEmailRequest(
        @NotBlank String from,
        @NotBlank String to,
        @NotBlank String subject,
        @NotBlank String body,
        String messageId,
        String inReplyTo,
        String threadId
) { }
