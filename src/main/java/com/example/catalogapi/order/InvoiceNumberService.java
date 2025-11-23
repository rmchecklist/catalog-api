package com.example.catalogapi.order;

import com.example.catalogapi.customer.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceNumberService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final InvoiceCounterRepository counterRepository;
    private final CustomerRepository customerRepository;

    public InvoiceNumberService(InvoiceCounterRepository counterRepository, CustomerRepository customerRepository) {
        this.counterRepository = counterRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public String nextInvoiceNumber(String customerCode) {
        var customer = customerRepository.findById(customerCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown customer code: " + customerCode));

        InvoiceCounterEntity counter = counterRepository.findById(customerCode)
                .orElseGet(() -> {
                    InvoiceCounterEntity c = new InvoiceCounterEntity();
                    c.setCustomerCode(customerCode);
                    c.setNextNumber(1);
                    return c;
                });

        long current = counter.getNextNumber();
        counter.setNextNumber(current + 1);
        counterRepository.save(counter);

        String datePart = LocalDate.now().format(DATE_FMT);
        String numPart = String.format("%03d", current);
        return "INV-" + datePart + "-" + customerCode + "-" + numPart;
    }
}
