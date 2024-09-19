package com.eg.invoice.controller;

import com.eg.invoice.dto.CreateBillingRequestDto;
import com.eg.invoice.dto.BillingDetailResponseDto;
import com.eg.invoice.dto.BillingResponseDto;
import com.eg.invoice.dto.OverdueBillingRequestDto;
import com.eg.invoice.dto.BillingPaymentRequestDto;
import com.eg.invoice.service.BillingDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/invoices")
@RestController
public class BillingController {

    private final BillingDetailService billingDetailService;

    @PostMapping
    public ResponseEntity<BillingResponseDto> createInvoice(@Valid @RequestBody CreateBillingRequestDto createInvoiceRequestDto) {
        return new ResponseEntity<>(billingDetailService.createInvoice(createInvoiceRequestDto),HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BillingDetailResponseDto>> getAllInvoices() {
        return new ResponseEntity<>(billingDetailService.getAllInvoices(),HttpStatus.OK);
    }

    @PostMapping(value = "/{invoiceId}/payments")
    public ResponseEntity<String> processPayment(@PathVariable(value = "invoiceId") Long id, @Valid @RequestBody BillingPaymentRequestDto paymentRequestDto) {
        billingDetailService.processPayment(id,paymentRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/process-overdue")
    public ResponseEntity<String> processOverduePayment(@Valid @RequestBody OverdueBillingRequestDto paymentOverdueInvoiceRequestDto) {
        billingDetailService.processOverduePayment(paymentOverdueInvoiceRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
