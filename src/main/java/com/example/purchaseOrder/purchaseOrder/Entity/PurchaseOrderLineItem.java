package com.example.purchaseOrder.purchaseOrder.Entity;

import com.example.purchaseOrder.purchaseOrder.CompositeKeys.PurchaseOrderLineItemId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class PurchaseOrderLineItem {

    @EmbeddedId
    private PurchaseOrderLineItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("poId")
    @JoinColumn(name = "po_id")
    @JsonBackReference
    private PurchaseOrder purchaseOrder;

    private Long productId;
    private Integer quantity;
    private BigDecimal price;

    // Helper methods for easier mapping
    public Long getLineItemId() {
        return id != null ? id.getLineItemId() : null;
    }
    public void setLineItemId(Long lineItemId) {
        if (id == null) id = new PurchaseOrderLineItemId();
        id.setLineItemId(lineItemId);
    }
}
