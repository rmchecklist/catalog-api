package com.example.catalogapi.customer;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @Column(length = 64)
    private String code; // unique abbreviation used in invoice number

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private CustomerType type = CustomerType.BUSINESS;

    private String primaryFirstName;
    private String primaryLastName;
    private String displayName;
    private String email;
    private String phoneWork;
    private String phoneMobile;
    private String company;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "attention", column = @Column(name = "billing_attention")),
            @AttributeOverride(name = "address1", column = @Column(name = "billing_address1")),
            @AttributeOverride(name = "address2", column = @Column(name = "billing_address2")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
            @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
            @AttributeOverride(name = "zip", column = @Column(name = "billing_zip")),
            @AttributeOverride(name = "phone", column = @Column(name = "billing_phone"))
    })
    private AddressEmbeddable billingAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "attention", column = @Column(name = "shipping_attention")),
            @AttributeOverride(name = "address1", column = @Column(name = "shipping_address1")),
            @AttributeOverride(name = "address2", column = @Column(name = "shipping_address2")),
            @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "state", column = @Column(name = "shipping_state")),
            @AttributeOverride(name = "zip", column = @Column(name = "shipping_zip")),
            @AttributeOverride(name = "phone", column = @Column(name = "shipping_phone"))
    })
    private AddressEmbeddable shippingAddress;

    @ElementCollection
    @CollectionTable(name = "customer_contacts", joinColumns = @JoinColumn(name = "customer_code"))
    private List<ContactPersonEmbeddable> contacts = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public String getPrimaryFirstName() {
        return primaryFirstName;
    }

    public void setPrimaryFirstName(String primaryFirstName) {
        this.primaryFirstName = primaryFirstName;
    }

    public String getPrimaryLastName() {
        return primaryLastName;
    }

    public void setPrimaryLastName(String primaryLastName) {
        this.primaryLastName = primaryLastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public AddressEmbeddable getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(AddressEmbeddable billingAddress) {
        this.billingAddress = billingAddress;
    }

    public AddressEmbeddable getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressEmbeddable shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<ContactPersonEmbeddable> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactPersonEmbeddable> contacts) {
        this.contacts = contacts;
    }
}
