package com.example.catalogapi.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TargetEmailRequest(@Email @NotBlank String email) { }
