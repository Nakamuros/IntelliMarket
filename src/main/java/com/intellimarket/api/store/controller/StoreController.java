package com.intellimarket.api.store.controller;

import com.intellimarket.api.store.dto.StoreRequest;
import com.intellimarket.api.store.dto.StoreResponse;
import com.intellimarket.api.store.service.IStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final IStoreService storeService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<StoreResponse> createStore(
            Authentication authentication,
            @Valid @RequestBody StoreRequest request) {
        String email = authentication.getName();
        return new ResponseEntity<>(storeService.createStore(email, request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StoreResponse>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStoreById(id));
    }

    @GetMapping("/my-store")
    public ResponseEntity<?> getMyStore(Authentication authentication) {
        String email = authentication.getName();
        return storeService.findByOwnerEmail(email)
                .map(store -> ResponseEntity.ok(Map.of("id", store.getId(), "name", store.getName())))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar datos comerciales de una tienda existente")
    @ApiResponse(responseCode = "200", description = "Datos de la tienda actualizados con éxito")
    @PutMapping("/{id}")
    public ResponseEntity<StoreResponse> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreRequest request) {

        System.out.println("[STORE-CONTROLLER] Solicitud de actualización para la tienda ID: " + id);
        StoreResponse response = storeService.updateStore(id, request);
        return ResponseEntity.ok(response);
    }
}
