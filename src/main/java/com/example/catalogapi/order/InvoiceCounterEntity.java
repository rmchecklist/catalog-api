package com.example.catalogapi.order;

import jakarta.persistence.*;

@Entity
@Table(name = "invoice_counters")
public class InvoiceCounterEntity {
    @Id
    @Column(length = 64)
    private String customerCode;

    private long nextNumber;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public long getNextNumber() {
        return nextNumber;
    }

    public void setNextNumber(long nextNumber) {
        this.nextNumber = nextNumber;
    }
}
