package com.intellimarket.api.supplier.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.intellimarket.api.supplier.dto.ProviderRequestDTO;
import com.intellimarket.api.supplier.dto.ProviderResponseDTO;
import com.intellimarket.api.supplier.service.ISupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // <-- ESTA ES LA MÁS IMPORTANTE
@RequestMapping("/api/v1/suppliers") // <-- ESTA LE DA LA DIRECCIÓN
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;

    @GetMapping("/test")
    public String test() {
        return "El controlador funciona";
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponseDTO>> getAllProviders() {
        return ResponseEntity.ok(supplierService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> getProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getProviderById(id));
    }

    @PostMapping
    public ResponseEntity<ProviderResponseDTO> createProvider(@Valid @RequestBody ProviderRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createProvider(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> updateProvider(
            @PathVariable Long id, 
            @Valid @RequestBody ProviderRequestDTO request) {
        return ResponseEntity.ok(supplierService.updateProvider(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        supplierService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}