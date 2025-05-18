package com.example.purchaseOrder.purchaseOrder.DTOs;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderDTO {
    private Long poId;
    private Long supplierId;
    private LocalDate poDate;
    private String status;
    private List<PurchaseOrderLineItemDTO> lineItems;
}
