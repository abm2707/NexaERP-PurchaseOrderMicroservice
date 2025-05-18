package com.example.purchaseOrder.purchaseOrder.Services;

import com.example.purchaseOrder.purchaseOrder.CompositeKeys.PurchaseOrderLineItemId;
import com.example.purchaseOrder.purchaseOrder.DTOs.PurchaseOrderDTO;
import com.example.purchaseOrder.purchaseOrder.DTOs.PurchaseOrderLineItemDTO;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrder;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrderLineItem;
import com.example.purchaseOrder.purchaseOrder.Repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    // Map DTO to Entity
    public PurchaseOrder mapToEntity(PurchaseOrderDTO dto) {
        PurchaseOrder entity = new PurchaseOrder();
        entity.setSupplierId(dto.getSupplierId());
        entity.setPoDate(dto.getPoDate());
        entity.setStatus(dto.getStatus());

        List<PurchaseOrderLineItem> lineItems = new ArrayList<>();
        if (dto.getLineItems() != null) {
            for (PurchaseOrderLineItemDTO itemDTO : dto.getLineItems()) {
                PurchaseOrderLineItem item = new PurchaseOrderLineItem();
                item.setProductId(itemDTO.getProductId());
                item.setQuantity(itemDTO.getQuantity());
                item.setPrice(itemDTO.getPrice());
                if (itemDTO.getLineItemId() != null) {
                    item.setLineItemId(itemDTO.getLineItemId());
                }
                lineItems.add(item);
            }
        }
        entity.setLineItems(lineItems);
        return entity;
    }

    public PurchaseOrder save(PurchaseOrder entity) {
        // 1. Copy and clear the existing collection (don't replace the reference!)
        List<PurchaseOrderLineItem> items = new ArrayList<>(entity.getLineItems());
        entity.getLineItems().clear(); // This is safe!

        // 2. Save parent to generate poId
        PurchaseOrder savedParent = purchaseOrderRepository.save(entity);

        // 3. Set parent and composite key on each line item
        long lineItemIdCounter = 1;
        for (PurchaseOrderLineItem item : items) {
            Long lineItemId = item.getLineItemId() != null ? item.getLineItemId() : lineItemIdCounter++;
            item.setId(new PurchaseOrderLineItemId(savedParent.getPoId(), lineItemId));
            item.setPurchaseOrder(savedParent);
            savedParent.getLineItems().add(item); // Add back to the original collection
        }

        // 4. Save again to persist line items
        return purchaseOrderRepository.save(savedParent);
    }



    public Optional<PurchaseOrder> findById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    // Map Entity to DTO
    public PurchaseOrderDTO mapToDTO(PurchaseOrder entity) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO();
        dto.setPoId(entity.getPoId());
        dto.setSupplierId(entity.getSupplierId());
        dto.setPoDate(entity.getPoDate());
        dto.setStatus(entity.getStatus());

        if (entity.getLineItems() != null) {
            List<PurchaseOrderLineItemDTO> lineItemDTOs = new ArrayList<>();
            for (PurchaseOrderLineItem item : entity.getLineItems()) {
                PurchaseOrderLineItemDTO itemDTO = new PurchaseOrderLineItemDTO();
                if (item.getId() != null) {
                    itemDTO.setLineItemId(item.getId().getLineItemId());
                }
                itemDTO.setProductId(item.getProductId());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                lineItemDTOs.add(itemDTO);
            }
            dto.setLineItems(lineItemDTOs);
        }
        return dto;
    }
}
