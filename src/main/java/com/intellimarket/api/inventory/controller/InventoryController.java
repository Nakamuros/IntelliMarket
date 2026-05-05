package com.intellimarket.api.inventory.controller;

import com.intellimarket.api.inventory.dto.ProductsRequest;
import com.intellimarket.api.inventory.dto.ProductsResponse;
import com.intellimarket.api.inventory.service.IInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
//@RequestMapping("/api/inventory/stores/{storeId}")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "Operaciones de la épica de Inventario (IntelliMarket)")

public class InventoryController {
    private final IInventoryService inventoryService;

    @Operation(summary = "Registrar un nuevo producto (US-05)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/products")
    public ResponseEntity<ProductsResponse> create(@Valid @RequestBody ProductsRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createProduct(request));
        //[cite: 1]
    }

    @Operation(summary = "US-06: Edición de Producto y Stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto o Tienda no encontrados")
    })
    @PutMapping("/products/{productId}")
    // De inventory a ruta de products, esta al id de products para identificación
    public ResponseEntity<ProductsResponse> update(
            @PathVariable Long productId,
            @PathVariable Long storeId,
            @Valid @RequestBody ProductsRequest request) {
        return ResponseEntity.ok(inventoryService.updateProduct(productId, storeId, request));
        //[cite: 1]
    }

    @Operation(summary = "US-07: Consulta de stock real por tienda o bodega")
    @ApiResponse(responseCode = "200", description = "Lista de stock recuperada")
    @GetMapping("/stock")
    public ResponseEntity<List<ProductsResponse>> listStock(@PathVariable Long storeId){
        return ResponseEntity.ok(inventoryService.getStockByStore(storeId));
        //[cite: 1]
    }

    @Operation(summary = "US-08: Alertas de Stock Crítico")
    @ApiResponse(responseCode = "200", description = "Lista de alertas generada")
    @GetMapping("/alerts")
    public ResponseEntity<List<ProductsResponse>> listAlerts(@PathVariable Long storeId) {
        return ResponseEntity.ok(inventoryService.getCriticalStock(storeId));
        //[cite: 1]
    }

    @Operation(summary = "US-09: Eliminación por Agotamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado o retirado del inventario"),
            @ApiResponse(responseCode = "404", description = "Relación producto-tienda no encontrada")
    })
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId, @PathVariable Long storeId) {
        inventoryService.deleteProduct(productId, storeId);
        return ResponseEntity.noContent().build();
        //[cite: 1]
    }
}
