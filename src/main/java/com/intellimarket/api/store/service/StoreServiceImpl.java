package com.intellimarket.api.store.service;

import com.intellimarket.api.auth.model.User;
import com.intellimarket.api.auth.repository.UserRepository;
import com.intellimarket.api.shared.exception.ResourceNotFoundException;
import com.intellimarket.api.store.dto.StoreRequest;
import com.intellimarket.api.store.dto.StoreResponse;
import com.intellimarket.api.store.mapper.StoreMapper;
import com.intellimarket.api.store.model.Store;
import com.intellimarket.api.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements IStoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreMapper storeMapper;

    @Override
    @Transactional
    public StoreResponse createStore(String ownerEmail, StoreRequest request) {
        User owner = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Store store = Store.builder()
            .name(request.name())
            .address(request.address())
            .district(request.district())
            .owner(owner)
            .isActive(true)
            .build();

        return storeMapper.toResponse(storeRepository.save(store));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponse> getAllStores() {
        return storeRepository.findAll().stream()
            .map(storeMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse getStoreById(Long id) {
        return storeRepository.findById(id)
            .map(storeMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Store> findByOwnerEmail(String email) {
        return storeRepository.findByOwnerEmail(email); // asumiendo que Store tiene owner (User)
    }
}