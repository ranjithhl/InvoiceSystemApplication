package com.eg.invoice.dao;

import com.eg.invoice.constant.enums.BillingStatusEnum;

import com.eg.invoice.entity.BillingDetailEntity;
import com.eg.invoice.repository.BillingDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.eg.invoice.dao.BillingDetailDao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BillingDetailDaoTest {

    @InjectMocks
    private BillingDetailDao billingDetailDao;

    @Mock
    private BillingDetailRepository billingDetailRepository;

    @Test
    void testSaveInvoice() {
    	BillingDetailEntity billingDetailEntity = new BillingDetailEntity();
    	billingDetailEntity.setStatus(BillingStatusEnum.PENDING);
    	billingDetailEntity.setAmount(400.00);
    	billingDetailEntity.setPaidAmount(0.0);
    	billingDetailEntity.setDueDate(LocalDate.now().plusDays(10));
        when(billingDetailRepository.save(any())).thenReturn(billingDetailEntity);

        BillingDetailEntity saveInvoiceResponse = billingDetailDao.saveInvoice(billingDetailEntity);
        assertNotNull(saveInvoiceResponse);
        assertEquals(billingDetailEntity.getAmount(),saveInvoiceResponse.getAmount());
        assertEquals(billingDetailEntity.getStatus(),saveInvoiceResponse.getStatus());
    }

    @Test
    void testFindAllInvoices() {
    	BillingDetailEntity billingDetailEntity = new BillingDetailEntity();
    	billingDetailEntity.setId(6L);
    	billingDetailEntity.setStatus(BillingStatusEnum.PENDING);
    	billingDetailEntity.setAmount(400.00);
    	billingDetailEntity.setPaidAmount(0.0);
    	billingDetailEntity.setDueDate(LocalDate.now().plusDays(10));

        when(billingDetailRepository.findAll()).thenReturn(List.of(billingDetailEntity));

        List<BillingDetailEntity> invoiceDetailResponse = billingDetailDao.findAllInvoices();
        assertNotNull(invoiceDetailResponse);
        assertFalse(invoiceDetailResponse.isEmpty());
        assertEquals(1, invoiceDetailResponse.size());
    }

    @Test
    void testFindInvoiceDetailById() {
    	BillingDetailEntity billingDetailEntity = new BillingDetailEntity();
    	billingDetailEntity.setId(6L);
    	billingDetailEntity.setStatus(BillingStatusEnum.PENDING);
    	billingDetailEntity.setAmount(400.00);
    	billingDetailEntity.setPaidAmount(0.0);
    	billingDetailEntity.setDueDate(LocalDate.now().plusDays(10));

        when(billingDetailRepository.findById(any())).thenReturn(Optional.of(billingDetailEntity));

        Optional<BillingDetailEntity> invoiceDetailResponse = billingDetailDao.findInvoiceDetailById(6L);
        assertNotNull(invoiceDetailResponse);
        assertTrue(invoiceDetailResponse.isPresent());
        assertEquals(billingDetailEntity, invoiceDetailResponse.get());
    }
}