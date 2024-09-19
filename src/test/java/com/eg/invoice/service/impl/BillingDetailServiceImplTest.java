package com.eg.invoice.service.impl;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BillingDetailServiceImplTest {

    @InjectMocks
    private BillingDetailServiceImpl billingDetailServiceImpl;

    @Mock
    private BillingDetailDao iillingDetailDao;

    @Test
    void testCreateInvoice() {
        CreateBillingRequestDto CreateBillingRequestDto = new CreateBillingRequestDto();
        CreateBillingRequestDto.setAmount(400.00);
        CreateBillingRequestDto.setDueDate(LocalDate.now().plusDays(10));

        BillingDetailEntity BillingDetailEntity = new BillingDetailEntity();
        BillingDetailEntity.setId(6L);
        BillingDetailEntity.setStatus(BillingStatusEnum.PENDING);
        BillingDetailEntity.setAmount(400.00);
        BillingDetailEntity.setPaidAmount(0.0);
        BillingDetailEntity.setDueDate(LocalDate.now().plusDays(10));

        when(iillingDetailDao.saveInvoice(any())).thenReturn(BillingDetailEntity);

        BillingResponseDto invoiceResponse = billingDetailServiceImpl.createInvoice(CreateBillingRequestDto);
        assertNotNull(invoiceResponse);
        assertEquals(6L, invoiceResponse.getId());
    }

    @Test
    void getAllInvoices() {
        BillingDetailEntity BillingDetailEntity = new BillingDetailEntity();
        BillingDetailEntity.setId(1L);
        BillingDetailEntity.setStatus(BillingStatusEnum.PENDING);
        BillingDetailEntity.setAmount(400.00);
        BillingDetailEntity.setPaidAmount(0.0);
        BillingDetailEntity.setDueDate(LocalDate.now().plusDays(10));

        BillingDetailEntity BillingDetailEntity1 = new BillingDetailEntity();
        BillingDetailEntity1.setId(2L);
        BillingDetailEntity1.setStatus(BillingStatusEnum.PENDING);
        BillingDetailEntity1.setAmount(1800.00);
        BillingDetailEntity1.setPaidAmount(0.0);
        BillingDetailEntity1.setDueDate(LocalDate.now().plusDays(10));

        when(iillingDetailDao.findAllInvoices()).thenReturn(List.of(BillingDetailEntity, BillingDetailEntity1));

        List<BillingDetailResponseDto> invoiceResponse = billingDetailServiceImpl.getAllInvoices();
        assertNotNull(invoiceResponse);
        assertFalse(invoiceResponse.isEmpty());
        assertEquals(2, invoiceResponse.size());
        assertEquals(1, invoiceResponse.get(0).getId());
    }

    @Test
    void processPaymentPartialPay() {
        BillingPaymentRequestDto BillingPaymentRequestDto = new BillingPaymentRequestDto();
        BillingPaymentRequestDto.setAmount(500.00);

        BillingDetailEntity BillingDetailEntity = new BillingDetailEntity();
        BillingDetailEntity.setId(1L);
        BillingDetailEntity.setStatus(BillingStatusEnum.PENDING);
        BillingDetailEntity.setAmount(400.00);
        BillingDetailEntity.setPaidAmount(0.0);
        BillingDetailEntity.setDueDate(LocalDate.now().plusDays(10));

        when(iillingDetailDao.findInvoiceDetailById(any())).thenReturn(Optional.of(BillingDetailEntity));

        when(iillingDetailDao.saveInvoice(any())).thenReturn(BillingDetailEntity);

        billingDetailServiceImpl.processPayment(1L, BillingPaymentRequestDto);
        verify(iillingDetailDao, times(1)).findInvoiceDetailById(any());
        verify(iillingDetailDao, times(1)).saveInvoice(any());
    }

    @Test
    void processPaymentFullPay() {
        BillingPaymentRequestDto BillingPaymentRequestDto = new BillingPaymentRequestDto();
        BillingPaymentRequestDto.setAmount(400.00);

        BillingDetailEntity BillingDetailEntity = new BillingDetailEntity();
        BillingDetailEntity.setId(1L);
        BillingDetailEntity.setStatus(BillingStatusEnum.PENDING);
        BillingDetailEntity.setAmount(400.00);
        BillingDetailEntity.setPaidAmount(0.0);
        BillingDetailEntity.setDueDate(LocalDate.now().plusDays(10));

        when(iillingDetailDao.findInvoiceDetailById(any())).thenReturn(Optional.of(BillingDetailEntity));

        when(iillingDetailDao.saveInvoice(any())).thenReturn(BillingDetailEntity);

        billingDetailServiceImpl.processPayment(1L, BillingPaymentRequestDto);
        verify(iillingDetailDao, times(1)).findInvoiceDetailById(any());
        verify(iillingDetailDao, times(1)).saveInvoice(any());
    }


    @Test
    void processPaymentInvoiceNotExist() {
        BillingPaymentRequestDto BillingPaymentRequestDto = new BillingPaymentRequestDto();
        BillingPaymentRequestDto.setAmount(400.00);

        when(iillingDetailDao.findInvoiceDetailById(any())).thenReturn(Optional.empty());

        assertThrows(BillingNotExistException.class, () -> billingDetailServiceImpl.processPayment(1L, BillingPaymentRequestDto));
    }

    @Test
    void processOverduePayment() {
        OverdueBillingRequestDto OverdueBillingRequestDto = new OverdueBillingRequestDto();
        OverdueBillingRequestDto.setLateFee(60.00);
        OverdueBillingRequestDto.setOverdueDays(8);

        BillingDetailEntity BillingDetailEntity = new BillingDetailEntity();
        BillingDetailEntity.setId(1L);
        BillingDetailEntity.setStatus(BillingStatusEnum.PENDING);
        BillingDetailEntity.setAmount(400.00);
        BillingDetailEntity.setPaidAmount(0.0);
        BillingDetailEntity.setDueDate(LocalDate.now().minusDays(10));

        BillingDetailEntity BillingDetailEntity1 = new BillingDetailEntity();
        BillingDetailEntity1.setId(2L);
        BillingDetailEntity1.setStatus(BillingStatusEnum.PENDING);
        BillingDetailEntity1.setAmount(1000.00);
        BillingDetailEntity1.setPaidAmount(500.00);
        BillingDetailEntity1.setDueDate(LocalDate.now().minusDays(10));

        when(iillingDetailDao.findAllInvoices()).thenReturn(List.of(BillingDetailEntity, BillingDetailEntity1));

        billingDetailServiceImpl.processOverduePayment(OverdueBillingRequestDto);
        verify(iillingDetailDao, times(1)).findAllInvoices();
    }

    @Test
    void processOverduePaymentNotFound() {
        OverdueBillingRequestDto OverdueBillingRequestDto = new OverdueBillingRequestDto();
        OverdueBillingRequestDto.setLateFee(60.00);
        OverdueBillingRequestDto.setOverdueDays(8);

        when(iillingDetailDao.findAllInvoices()).thenReturn(List.of());
        assertThrows(BillingNotExistException.class, () -> billingDetailServiceImpl.processOverduePayment(OverdueBillingRequestDto));
    }

}