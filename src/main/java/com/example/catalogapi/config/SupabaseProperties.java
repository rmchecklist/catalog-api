package com.example.catalogapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "supabase")
public record SupabaseProperties(
        String projectUrl,
        String serviceKey,
        String storageBucket
) {}
