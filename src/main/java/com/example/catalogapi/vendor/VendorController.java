package com.example.catalogapi.vendor;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/vendors")
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VendorResponse>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<VendorResponse> upsert(@Valid @RequestBody VendorRequest request) {
        VendorResponse saved = service.upsert(request);
        return ResponseEntity.created(URI.create("/api/admin/vendors/" + saved.code())).body(saved);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
