package com.example.catalogapi.vendor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VendorService {

    private final VendorRepository repository;

    public VendorService(VendorRepository repository) {
        this.repository = repository;
    }

    public List<VendorResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public VendorResponse upsert(VendorRequest request) {
        VendorEntity entity = repository.findById(request.code()).orElseGet(VendorEntity::new);
        entity.setCode(request.code());
        entity.setName(request.name());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setCompany(request.company());
        entity.setAddress(request.address());
        repository.save(entity);
        return toResponse(entity);
    }

    public void delete(String code) {
        repository.findById(code).ifPresent(repository::delete);
    }

    private VendorResponse toResponse(VendorEntity entity) {
        return new VendorResponse(entity.getCode(), entity.getName(), entity.getEmail(), entity.getPhone(),
                entity.getCompany(), entity.getAddress());
    }
}
