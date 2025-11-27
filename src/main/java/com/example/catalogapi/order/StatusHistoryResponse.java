package com.example.catalogapi.order;

import java.time.OffsetDateTime;

public record StatusHistoryResponse(String status, OffsetDateTime changedAt) { }
