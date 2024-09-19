package com.eg.invoice.repository;


import com.eg.invoice.entity.BillingDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDetailRepository extends JpaRepository<BillingDetailEntity, Long> {

}
