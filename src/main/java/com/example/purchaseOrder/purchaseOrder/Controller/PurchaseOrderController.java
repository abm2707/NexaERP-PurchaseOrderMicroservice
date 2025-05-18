package com.example.purchaseOrder.purchaseOrder.Controller;

import com.example.purchaseOrder.purchaseOrder.DTOs.PurchaseOrderLineItemDTO;
import com.example.purchaseOrder.purchaseOrder.DTOs.PurchaseOrderDTO;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrder;
import com.example.purchaseOrder.purchaseOrder.Services.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderDTO> createPO(@RequestBody PurchaseOrderDTO poDTO) {
        PurchaseOrder entity = purchaseOrderService.mapToEntity(poDTO);
        PurchaseOrder savedEntity = purchaseOrderService.save(entity);
        PurchaseOrderDTO responseDTO = purchaseOrderService.mapToDTO(savedEntity);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getPO(@PathVariable Long id) {
        return purchaseOrderService.findById(id)
                .map(po -> ResponseEntity.ok(purchaseOrderService.mapToDTO(po)))
                .orElse(ResponseEntity.notFound().build());
    }
}
