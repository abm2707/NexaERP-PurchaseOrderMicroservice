package com.example.purchaseOrder.purchaseOrder.Controller;

import com.example.purchaseOrder.purchaseOrder.DTOs.ApiResponse;
import com.example.purchaseOrder.purchaseOrder.DTOs.PurchaseOrderDTO;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrder;
import com.example.purchaseOrder.purchaseOrder.Entity.PurchaseOrderStatus;
import com.example.purchaseOrder.purchaseOrder.Services.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPO(@RequestBody PurchaseOrderDTO poDTO) {
        PurchaseOrderDTO created = purchaseOrderService.createPurchaseOrder(poDTO);
        return ResponseEntity.ok(new ApiResponse<>("Purchase order created", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPO(@PathVariable Long id) {
        PurchaseOrderDTO po = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(new ApiResponse<>("Purchase order retrieved", po));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseOrderDTO>>> getAllPOs(
            Pageable pageable,
            @RequestParam(required = false) PurchaseOrderStatus status) {
        Page<PurchaseOrderDTO> result = purchaseOrderService.getAllPurchaseOrders(pageable, status);
        return ResponseEntity.ok(new ApiResponse<>("Purchase orders retrieved", result));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam PurchaseOrderStatus newStatus) {
        PurchaseOrderDTO updated = purchaseOrderService.updatePurchaseOrderStatus(id, newStatus);
        return ResponseEntity.ok(new ApiResponse<>("Status updated", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePO(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(new ApiResponse<>("Purchase order deleted"));
    }
}


