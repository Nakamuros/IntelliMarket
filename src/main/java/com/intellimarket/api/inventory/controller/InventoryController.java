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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> create(@RequestParam Long storeId,
                                                   @Valid @RequestBody ProductsRequest request){
        /*return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createProduct(storeId, request));*/
        ProductsResponse response = inventoryService.createProduct(storeId, request);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Creación correcta");
        body.put("data", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(summary = "US-06: Edición de Producto y Stock")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto o Tienda no encontrados")
    })
    @PutMapping("/products/{productId}")
    // De inventory a ruta de products, esta al id de products para identificación
    public ResponseEntity<?> update(@PathVariable Long productId, @RequestParam Long storeId, @Valid @RequestBody ProductsRequest request) {
        ProductsResponse response = inventoryService.updateProduct(productId, storeId, request);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Edición correcta");
        body.put("data", response);

        return ResponseEntity.ok(body);
    }

    @Operation(summary = "US-07: Consulta de stock real por tienda o bodega")
    @ApiResponse(responseCode = "200", description = "Lista de stock recuperada")
    @GetMapping("/stock")
    public ResponseEntity<List<ProductsResponse>> listStock(@RequestParam Long storeId){
        return ResponseEntity.ok(inventoryService.getStockByStore(storeId));
        //[cite: 1]
    }

    @Operation(summary = "US-08: Alertas de Stock Crítico")
    @ApiResponse(responseCode = "200", description = "Lista de alertas generada")
    @GetMapping("/alerts")
    public ResponseEntity<?> listAlerts(@RequestParam Long storeId) {
        List<ProductsResponse> alerts = inventoryService.getCriticalStock(storeId);

        if (alerts.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No se encontraron alertas de stock en su tienda");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(alerts);
    }

    /*@Operation(summary = "US-09: Eliminación por Agotamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar un producto con stock"),
            @ApiResponse(responseCode = "404", description = "Relación producto-tienda no encontrada")
    })
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> changeStatus(@RequestParam Long storeId) {
        inventoryService.changeState(storeId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Estado de ");

        return ResponseEntity.ok(response);
    }*/
}