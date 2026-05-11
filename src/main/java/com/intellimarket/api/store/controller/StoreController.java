package com.intellimarket.api.store.controller;

import com.intellimarket.api.store.dto.StoreRequest;
import com.intellimarket.api.store.dto.StoreResponse;
import com.intellimarket.api.store.service.IStoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
