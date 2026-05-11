package com.intellimarket.api.store.service;

import com.intellimarket.api.store.dto.StoreRequest;
import com.intellimarket.api.store.dto.StoreResponse;
import java.util.List;

public interface IStoreService {
    StoreResponse createStore(String ownerEmail, StoreRequest request);
    List<StoreResponse> getAllStores();
    StoreResponse getStoreById(Long id);
}
