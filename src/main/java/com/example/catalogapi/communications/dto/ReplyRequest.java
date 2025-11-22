package com.example.catalogapi.communications.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReplyRequest(
        @NotBlank @Email String from,
        @NotBlank @Email String to,
        String cc,
        String bcc,
        @NotBlank String body
) {
}
