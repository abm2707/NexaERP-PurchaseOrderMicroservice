package com.example.purchaseOrder.purchaseOrder.Repository;

import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

}
