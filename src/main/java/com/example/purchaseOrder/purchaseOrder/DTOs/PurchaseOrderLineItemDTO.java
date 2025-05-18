package com.example.purchaseOrder.purchaseOrder.DTOs;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderLineItemDTO {
    private Long lineItemId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
