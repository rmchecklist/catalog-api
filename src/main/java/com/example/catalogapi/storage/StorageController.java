package com.example.catalogapi.storage;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequestMapping("/api/storage")
public class StorageController {

    private final SupabaseS3StorageService supabaseS3StorageService;

    public StorageController(SupabaseS3StorageService supabaseS3StorageService) {
        this.supabaseS3StorageService = supabaseS3StorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String key = supabaseS3StorageService.uploadFile(file);

            return ResponseEntity.ok(Map.of(
                    "file", key,
                    "url", supabaseS3StorageService.getPublicReadUrl(key).toString()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
}
