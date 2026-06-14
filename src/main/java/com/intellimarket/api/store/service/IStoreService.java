package com.intellimarket.api.store.service;

import com.intellimarket.api.store.dto.StoreRequest;
import com.intellimarket.api.store.dto.StoreResponse;
import com.intellimarket.api.store.model.Store;

import java.util.List;
import java.util.Optional;

public interface IStoreService {
    StoreResponse createStore(String ownerEmail, StoreRequest request);
    List<StoreResponse> getAllStores();
    StoreResponse getStoreById(Long id);
    Optional<Store> findByOwnerEmail(String email);
}
