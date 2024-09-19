package com.eg.invoice.service;

import com.eg.invoice.dto.CreateBillingRequestDto;
import com.eg.invoice.dto.BillingDetailResponseDto;
import com.eg.invoice.dto.BillingResponseDto;
import com.eg.invoice.dto.OverdueBillingRequestDto;
import com.eg.invoice.dto.BillingPaymentRequestDto;

import java.util.List;

public interface BillingDetailService {

    BillingResponseDto createInvoice(CreateBillingRequestDto createInvoiceRequestDto);

    List<BillingDetailResponseDto> getAllInvoices();

    void processPayment(Long id, BillingPaymentRequestDto paymentRequestDto);

    void processOverduePayment(OverdueBillingRequestDto paymentOverdueInvoiceRequestDto);
}
