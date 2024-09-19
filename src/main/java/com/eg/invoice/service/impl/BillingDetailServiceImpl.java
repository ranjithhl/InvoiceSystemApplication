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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BillingDetailServiceImpl implements BillingDetailService {

    private final BillingDetailDao invoiceDetailDao;

    @Override
    public BillingResponseDto createInvoice(CreateBillingRequestDto createBillingRequestDto) {
        BillingDetailEntity invoice = new BillingDetailEntity();
        invoice.setAmount(createBillingRequestDto.getAmount());
        invoice.setDueDate(createBillingRequestDto.getDueDate());
        invoice.setPaidAmount(0.0);
        invoice.setStatus(BillingStatusEnum.PENDING);
        invoice = invoiceDetailDao.saveInvoice(invoice);

        return new BillingResponseDto(invoice.getId());
    }

    @Override
    public List<BillingDetailResponseDto> getAllInvoices() {
        return invoiceDetailDao.findAllInvoices().stream()
                .map(invoice -> new BillingDetailResponseDto(
                        invoice.getId(),
                        invoice.getAmount(),
                        invoice.getPaidAmount(),
                        invoice.getDueDate(),
                        invoice.getStatus().getValue()
                ))
                .toList();
    }

    @Override
    public void processPayment(Long id, BillingPaymentRequestDto paymentRequestDto) {
        // Fetch invoice and validate existence
        BillingDetailEntity billingDetailEntity = fetchInvoice(id);

        Double totalPaidAmount = billingDetailEntity.getPaidAmount() + paymentRequestDto.getAmount();
        billingDetailEntity.setPaidAmount(totalPaidAmount);
        if (totalPaidAmount.equals(billingDetailEntity.getAmount())) {
        	billingDetailEntity.setStatus(BillingStatusEnum.PAID);
        }
        invoiceDetailDao.saveInvoice(billingDetailEntity);
    }

    private BillingDetailEntity fetchInvoice(Long id) {
        return invoiceDetailDao.findInvoiceDetailById(id)
                .orElseThrow(() -> new BillingNotExistException("Invoice not exist"));
    }

    @Override
    public void processOverduePayment(OverdueBillingRequestDto overdueBillingRequestDto) {

        LocalDate overdueDate = LocalDate.now().minusDays(overdueBillingRequestDto.getOverdueDays());
        List<BillingDetailEntity> invoiceDetailList = invoiceDetailDao.findAllInvoices().stream()
                .filter(invoice -> BillingStatusEnum.PENDING.equals(invoice.getStatus()) &&
                        invoice.getDueDate().isBefore(overdueDate))
                .toList();

        if (CollectionUtils.isEmpty(invoiceDetailList)) {
            throw new BillingNotExistException("No overdue invoices exist");
        }

        processLatePayments(overdueBillingRequestDto.getLateFee(),
                overdueBillingRequestDto.getOverdueDays(),
                invoiceDetailList,
                LocalDate.now());
    }

    private void processLatePayments(Double lateFee, Integer overdueDays, List<BillingDetailEntity> invoiceList, LocalDate currentDate) {
        invoiceList.forEach(invoice -> {
            Double remainingAmount = invoice.getAmount() - invoice.getPaidAmount();
            BillingStatusEnum newStatus = invoice.getPaidAmount() > 0 && invoice.getPaidAmount() < invoice.getAmount()
                    ? BillingStatusEnum.PAID
                    : BillingStatusEnum.VOID;

            invoice.setStatus(newStatus);

            saveInvoiceDetail(remainingAmount + lateFee, currentDate.plusDays(overdueDays));
            invoiceDetailDao.saveInvoice(invoice);
        });
    }

    private void saveInvoiceDetail(Double amount, LocalDate dueDate) {
        BillingDetailEntity billingDetailEntity = new BillingDetailEntity();
        billingDetailEntity.setAmount(amount);
        billingDetailEntity.setDueDate(dueDate);
        billingDetailEntity.setPaidAmount(0.0);
        billingDetailEntity.setStatus(BillingStatusEnum.PENDING);
        invoiceDetailDao.saveInvoice(billingDetailEntity);
    }
}
