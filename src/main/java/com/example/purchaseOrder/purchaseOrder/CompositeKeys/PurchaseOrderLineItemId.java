package com.example.purchaseOrder.purchaseOrder.CompositeKeys;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PurchaseOrderLineItemId implements Serializable {
    private Long poId;
    private Long lineItemId;

    public PurchaseOrderLineItemId() {}

    public PurchaseOrderLineItemId(Long poId, Long lineItemId) {
        this.poId = poId;
        this.lineItemId = lineItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseOrderLineItemId)) return false;
        PurchaseOrderLineItemId that = (PurchaseOrderLineItemId) o;
        return Objects.equals(poId, that.poId) &&
                Objects.equals(lineItemId, that.lineItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poId, lineItemId);
    }
}
