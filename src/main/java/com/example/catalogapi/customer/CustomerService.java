package com.example.catalogapi.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<CustomerResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public CustomerResponse upsert(CustomerRequest request) {
        CustomerEntity entity = repository.findById(request.code()).orElseGet(CustomerEntity::new);
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

    private CustomerResponse toResponse(CustomerEntity entity) {
        return new CustomerResponse(entity.getCode(), entity.getName(), entity.getEmail(), entity.getPhone(),
                entity.getCompany(), entity.getAddress());
    }
}
