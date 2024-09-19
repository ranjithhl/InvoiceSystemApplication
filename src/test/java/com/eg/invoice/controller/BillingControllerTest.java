package com.eg.invoice.controller;


import com.eg.invoice.constant.enums.BillingStatusEnum;
import com.eg.invoice.dao.BillingDetailDao;
import com.eg.invoice.dto.CreateBillingRequestDto;
import com.eg.invoice.dto.BillingDetailResponseDto;
import com.eg.invoice.dto.BillingResponseDto;
import com.eg.invoice.dto.OverdueBillingRequestDto;
import com.eg.invoice.dto.BillingPaymentRequestDto;
import com.eg.invoice.entity.BillingDetailEntity;
import com.eg.invoice.exception.BillingNotExistException;
import com.eg.invoice.service.BillingDetailService;
import com.eg.invoice.service.impl.BillingDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BillingControllerTest {

    @InjectMocks
    private BillingController billingController;

    @Mock
    private BillingDetailService billingDetailService;

    @Test
    void testCreateInvoice() {
    	CreateBillingRequestDto createBillingRequestDto = new CreateBillingRequestDto();
    	createBillingRequestDto.setAmount(500.00);
    	createBillingRequestDto.setDueDate(LocalDate.now().plusDays(10));

    	BillingResponseDto invoiceResponseDto = new BillingResponseDto(1L);

        when(billingDetailService.createInvoice(any())).thenReturn(invoiceResponseDto);

        ResponseEntity<BillingResponseDto> apiResponse = billingController.createInvoice(createBillingRequestDto);
        assertNotNull(apiResponse.getStatusCode());
        assertNotNull(apiResponse.getBody());
        assertEquals(HttpStatus.CREATED, apiResponse.getStatusCode());
        assertEquals(invoiceResponseDto.getId(), apiResponse.getBody().getId());
    }

    @Test
    void getAllInvoices() {
    	BillingDetailResponseDto billingDetailResponseDto = new BillingDetailResponseDto();
    	billingDetailResponseDto.setId(1L);
    	billingDetailResponseDto.setStatus(BillingStatusEnum.PAID.getValue());
    	billingDetailResponseDto.setAmount(400.00);
    	billingDetailResponseDto.setPaidAmount(0.0);
        billingDetailResponseDto.setDueDate(LocalDate.now().minusDays(10));

        when(billingDetailService.getAllInvoices()).thenReturn(List.of(billingDetailResponseDto));

        ResponseEntity<List<BillingDetailResponseDto>> apiResponse = billingController.getAllInvoices();
        assertNotNull(apiResponse.getStatusCode());
        assertNotNull(apiResponse.getBody());
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
        assertFalse(apiResponse.getBody().isEmpty());
        assertEquals(billingDetailResponseDto, apiResponse.getBody().get(0));
    }

    @Test
    void processPayment() {
    	BillingPaymentRequestDto billingPaymentRequestDto = new BillingPaymentRequestDto();
    	billingPaymentRequestDto.setAmount(500.00);

    	BillingDetailResponseDto billingDetailResponseDto = new BillingDetailResponseDto();
    	billingDetailResponseDto.setId(1L);
    	billingDetailResponseDto.setStatus(BillingStatusEnum.PAID.getValue());
    	billingDetailResponseDto.setAmount(400.00);
    	billingDetailResponseDto.setPaidAmount(0.0);
    	billingDetailResponseDto.setDueDate(LocalDate.now().minusDays(10));

        ResponseEntity<String> apiResponse = billingController.processPayment(1L, billingPaymentRequestDto);
        assertNotNull(apiResponse.getStatusCode());
        assertNull(apiResponse.getBody());
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

    @Test
    void processOverduePayment() {
    	OverdueBillingRequestDto overdueBillingRequestDto = new OverdueBillingRequestDto();
        overdueBillingRequestDto.setLateFee(60.00);
        overdueBillingRequestDto.setOverdueDays(5);

        ResponseEntity<String> apiResponse = billingController.processOverduePayment(overdueBillingRequestDto);
        assertNotNull(apiResponse.getStatusCode());
        assertNull(apiResponse.getBody());
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

}