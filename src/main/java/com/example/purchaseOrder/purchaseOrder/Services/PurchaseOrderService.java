package com.example.purchaseOrder.purchaseOrder.Services;

import com.example.purchaseOrder.purchaseOrder.CompositeKeys.PurchaseOrderLineItemId;
import com.example.purchaseOrder.purchaseOrder.DTOs.PurchaseOrderDTO;
import com.example.purchaseOrder.purchaseOrder.DTOs.PurchaseOrderLineItemDTO;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrder;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrderLineItem;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrderStatus;
import com.example.purchaseOrder.purchaseOrder.Repository.PurchaseOrderRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.module.ResolutionException;
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

    @Transactional
    public PurchaseOrderDTO updatePurchaseOrderStatus(Long id, PurchaseOrderStatus newStatus) {
        // Find the purchase order by ID or throw exception
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResolutionException("PurchaseOrder not found with id: " + id));

        // Validate status transition logic
        if (!isValidStatusTransition(PurchaseOrderStatus.valueOf(po.getStatus()), newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", po.getStatus(), newStatus)
            );
        }

        // Update status
        po.setStatus(String.valueOf(newStatus));

        // Save and return
        PurchaseOrder updatedPO = purchaseOrderRepository.save(po);
        return mapToDTO(updatedPO);
    }

    // Helper method: Validate status transition
    private boolean isValidStatusTransition(PurchaseOrderStatus current, PurchaseOrderStatus newStatus) {
        // Example: Only allow DRAFT -> APPROVED, APPROVED -> SHIPPED, SHIPPED -> RECEIVED
        if (current == PurchaseOrderStatus.DRAFT && newStatus == PurchaseOrderStatus.APPROVED) {
            return true;
        }
        if (current == PurchaseOrderStatus.APPROVED && newStatus == PurchaseOrderStatus.SHIPPED) {
            return true;
        }
        if (current == PurchaseOrderStatus.SHIPPED && newStatus == PurchaseOrderStatus.RECEIVED) {
            return true;
        }
        // Add more transitions as needed
        return false;
    }

    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder not found with id: " + id));

        // Example business rule: Only DRAFT orders can be deleted
        if (!po.getStatus().equalsIgnoreCase("DRAFT")) {
            throw new IllegalStateException("Only DRAFT purchase orders can be deleted");
        }

        purchaseOrderRepository.delete(po);
    }

    public Page<PurchaseOrderDTO> getAllPurchaseOrders(Pageable pageable, PurchaseOrderStatus status) {
        if (status != null) {
            return purchaseOrderRepository.findByStatus(status, pageable)
                    .map(this::mapToDTO);
        } else {
            return purchaseOrderRepository.findAll(pageable)
                    .map(this::mapToDTO);
        }
    }

    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(PurchaseOrderDTO poDTO) {
        // Map DTO to Entity
        PurchaseOrder po = mapToEntity(poDTO);
        // Set initial status (e.g., DRAFT)
        po.setStatus(String.valueOf(PurchaseOrderStatus.DRAFT));
        // Optionally: Validate vendor and products here (if not done in DTO/entity)
        // Save to database
        PurchaseOrder savedPO = purchaseOrderRepository.save(po);
        // Map Entity back to DTO
        return mapToDTO(savedPO);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
    }


}
