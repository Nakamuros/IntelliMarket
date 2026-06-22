package com.intellimarket.api.inventory.service;

import com.intellimarket.api.inventory.dto.ProductsRequest;
import com.intellimarket.api.inventory.dto.ProductsResponse;
import com.intellimarket.api.inventory.mapper.ProductsMapper;
import com.intellimarket.api.inventory.model.Inventory;
import com.intellimarket.api.inventory.model.InventoryMovement;
import com.intellimarket.api.inventory.model.Reference_Type;
import com.intellimarket.api.inventory.model.Type;
import com.intellimarket.api.inventory.repository.InventoryMovementRepository;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.product.model.Product;
import com.intellimarket.api.product.repository.ProductRepository;
import com.intellimarket.api.shared.exception.ResourceNotFoundException;
import com.intellimarket.api.store.model.Store;
import com.intellimarket.api.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final ProductsMapper productsMapper;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public ProductsResponse createProduct(Long storeId, ProductsRequest request) {
        Product product = productRepository.save(Product.builder()
                .name(request.name())
                .category(request.category())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .unitPrice(request.unitPrice())
                .build());

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        Inventory inventory = inventoryRepository.save(Inventory.builder()
                .product(product)
                .store(store)
                .stock(request.stock())
                .price(request.unitPrice())
                .state(request.stock() > 0 ? 1 : 0)
                .build());

        saveMovement(product, store, request.stock(), Type.PURCHASE, Reference_Type.PROVIDER);

        return productsMapper.toResponse(product, inventory);
    }

    @Override
    @Transactional
    public ProductsResponse updateProduct(Long productId, Long storeId, ProductsRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no existe"));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no asignado a esta tienda"));

        product.setDescription(request.description());
        product.setName(request.name());
        product.setCategory(request.category());
        product.setUnitPrice(request.unitPrice());
        inventory.setStock(request.stock());
        product.setImageUrl(request.imageUrl());

        productRepository.save(product);
        inventoryRepository.save(inventory);

        return productsMapper.toResponse(product, inventory);
    }

    @Override
    public List<ProductsResponse> getStockByStore(Long storeId) {
        return inventoryRepository.findByStoreIdAndState(storeId, 1).stream()
                .map(inv -> productsMapper.toResponse(inv.getProduct(), inv))
                .toList();
    }

    @Override
    public List<ProductsResponse> getCriticalStock(Long storeId) {
        return inventoryRepository.findByStoreIdAndStockLessThan(storeId, 10).stream()
                .map(inv -> productsMapper.toResponse(inv.getProduct(), inv))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductsResponse getProductByIdAndStore(Long productId, Long storeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto solicitado no existe"));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no esta registrado en esta tienda"));

        return productsMapper.toResponse(product, inventory);
    }

    private void saveMovement(Product product, Store store, Integer quantity, Type type, Reference_Type referenceType) {
        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .store(store)
                .quantity(quantity)
                .type(type)
                .reference_type(referenceType)
                .build();
        inventoryMovementRepository.save(movement);
    }
}
