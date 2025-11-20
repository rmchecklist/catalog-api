package com.example.catalogapi.config;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public final class SupabaseJwtDecoder {
    private SupabaseJwtDecoder() {}

    public static JwtDecoder build(String secret) {
        Assert.hasText(secret, "Supabase JWT secret must be provided (supabase.jwt.secret)");
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
