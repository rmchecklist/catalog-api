package com.example.catalogapi.communications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SesSnsMessage(
        String Type,
        String Message,
        String MessageId,
        String Timestamp
) {}
