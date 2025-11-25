package com.example.catalogapi.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        entity.setType(request.type() != null ? request.type() : CustomerType.BUSINESS);
        entity.setPrimaryFirstName(request.primaryFirstName());
        entity.setPrimaryLastName(request.primaryLastName());
        entity.setDisplayName(request.displayName());
        entity.setEmail(request.email());
        entity.setPhoneWork(request.phoneWork());
        entity.setPhoneMobile(request.phoneMobile());
        entity.setCompany(request.company());
        entity.setBillingAddress(toAddress(request.billingAddress()));
        entity.setShippingAddress(toAddress(request.shippingAddress()));
        entity.setContacts(toContacts(request.contacts()));
        repository.save(entity);
        return toResponse(entity);
    }

    public void delete(String code) {
        repository.findById(code).ifPresent(repository::delete);
    }

    private CustomerResponse toResponse(CustomerEntity entity) {
        return new CustomerResponse(
                entity.getCode(),
                entity.getType() != null ? entity.getType() : CustomerType.BUSINESS,
                entity.getPrimaryFirstName(),
                entity.getPrimaryLastName(),
                entity.getDisplayName(),
                entity.getCompany(),
                entity.getEmail(),
                entity.getPhoneWork(),
                entity.getPhoneMobile(),
                toAddressDto(entity.getBillingAddress()),
                toAddressDto(entity.getShippingAddress()),
                toContactDtos(entity.getContacts())
        );
    }

    private AddressEmbeddable toAddress(CustomerRequest.AddressDto dto) {
        if (dto == null) return null;
        AddressEmbeddable address = new AddressEmbeddable();
        address.setAttention(dto.attention());
        address.setAddress1(dto.address1());
        address.setAddress2(dto.address2());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setZip(dto.zip());
        address.setPhone(dto.phone());
        return address;
    }

    private CustomerResponse.AddressDto toAddressDto(AddressEmbeddable embeddable) {
        if (embeddable == null) return null;
        return new CustomerResponse.AddressDto(
                embeddable.getAttention(),
                embeddable.getAddress1(),
                embeddable.getAddress2(),
                embeddable.getCity(),
                embeddable.getState(),
                embeddable.getZip(),
                embeddable.getPhone()
        );
    }

    private List<ContactPersonEmbeddable> toContacts(List<CustomerRequest.ContactPersonDto> contacts) {
        if (contacts == null) return List.of();
        List<ContactPersonEmbeddable> list = new ArrayList<>();
        for (CustomerRequest.ContactPersonDto dto : contacts) {
            ContactPersonEmbeddable c = new ContactPersonEmbeddable();
            c.setFirstName(dto.firstName());
            c.setLastName(dto.lastName());
            c.setEmail(dto.email());
            c.setWorkPhone(dto.workPhone());
            c.setMobilePhone(dto.mobilePhone());
            list.add(c);
        }
        return list;
    }

    private List<CustomerResponse.ContactPersonDto> toContactDtos(List<ContactPersonEmbeddable> contacts) {
        if (contacts == null) return List.of();
        return contacts.stream()
                .map(c -> new CustomerResponse.ContactPersonDto(
                        c.getFirstName(),
                        c.getLastName(),
                        c.getEmail(),
                        c.getWorkPhone(),
                        c.getMobilePhone()
                ))
                .toList();
    }
}
