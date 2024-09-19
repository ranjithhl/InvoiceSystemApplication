package com.eg.invoice.dao;

import com.eg.invoice.entity.BillingDetailEntity;
import com.eg.invoice.repository.BillingDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BillingDetailDao {

    private final BillingDetailRepository billingDetailRepository;

    public BillingDetailEntity saveInvoice(BillingDetailEntity invoiceEntity) {
        return billingDetailRepository.save(invoiceEntity);
    }

    public List<BillingDetailEntity> findAllInvoices() {
        return billingDetailRepository.findAll();
    }

    public Optional<BillingDetailEntity> findInvoiceDetailById(Long id) {
        return billingDetailRepository.findById(id);
    }

}
