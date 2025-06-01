package com.example.purchaseOrder.purchaseOrder.Repository;

import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrder;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable pageable);
}
