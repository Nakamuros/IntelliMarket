package com.intellimarket.api.inventory.service;

import com.intellimarket.api.inventory.dto.ProductsRequest;
import com.intellimarket.api.inventory.dto.ProductsResponse;
import com.intellimarket.api.inventory.mapper.ProductsMapper;
import com.intellimarket.api.inventory.model.Inventory;
import com.intellimarket.api.inventory.model.Inventory_Movements;
import com.intellimarket.api.inventory.model.Products;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.inventory.repository.Inventory_MovementsRepository;
import com.intellimarket.api.inventory.repository.ProductsRepository;
import com.intellimarket.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {
    private final ProductsRepository productsRepository;
    private final InventoryRepository inventoryRepository;
    private final Inventory_MovementsRepository inventoryMovementsRepository;
    private final ProductsMapper productsMapper;

    @Override
    @Transactional
    // Crear prodducto
    public ProductsResponse createProduct(ProductsRequest request) {
        // 1. Guardar el producto en su catálogo (Products)
        Products product = productsRepository.save(Products.builder().name(request.name())
        .category(request.category()).description(request.description()).build());

        // Guardar producto en bodega de tienda o su inventario (Inventory)
        Inventory inventory = inventoryRepository.save(Inventory.builder().product(product).
                store(request.store_id()).stock(request.stock()).price(request.price()).build());

        // Registrar movimiento de historial en Inventory_Movements
        saveMovement(product, request.store_id(), request.stock(), "IN", "INITIAL_LOAD");

        return productsMapper.toResponse(product, inventory);
    }

    // Editar producto
    @Override
    @Transactional
    public ProductsResponse updateProduct(Long product_id, Long store_id, ProductsRequest request){
        Products product = productsRepository.findById(product_id).orElseThrow(()-> new
                ResourceNotFoundException("Producto no existe"));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(product_id, store_id).
                orElseThrow(()-> new ResourceNotFoundException("Producto no asignado a esta tienda"));

        // Actualizamos según la US-06
        product.setDescription(request.description());
        product.setName(request.name());
        product.setCategory(request.category());
        inventory.setPrice(request.price());

        productsRepository.save(product);
        inventoryRepository.save(inventory);

        return productsMapper.toResponse(product, inventory);
    }

    @Override
    // Stock de productos por tienda
    public List<ProductsResponse> getStockByStore(Long store_id){
        // US-07
        return inventoryRepository.findByStoreId(store_id).stream().map(inv->productsMapper.
                toResponse(inv.getProduct(), inv)).toList();
    }

    @Override
    // Alerta de stock crítico con productos con menos de 10 unidades
    public List<ProductsResponse> getCriticalStock(Long store_id){
        return inventoryRepository.findByStoreIdAndStockLessThan(store_id, 10).stream().
                map(inv->productsMapper.toResponse(inv.getProduct(), inv)).toList();
    }

    @Override
    @Transactional
    // US-09: ELiminar producto
    public void deleteProduct(Long product_id, Long store_id){
        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(product_id, store_id).
                orElseThrow(()-> new ResourceNotFoundException("No se encontró el registro"));

        // US-09: Solo borrar si stock es 0
        /*if (inventory.getStock() == 0) {
            inventoryRepository.delete(inventory);
        } else {
            throw new IllegalStateException("No se puede eliminar un producto con stock.");
        }*/

        inventoryRepository.delete(inventory);
    }

    private void saveMovement(Products p, Long sId, Integer qty, String type, String ref) {
        Inventory_Movements m = Inventory_Movements.builder()
                .product_id(p)
                .store_id(sId)
                .quantity(qty)
                .type(type)
                .reference_type(ref)
                .build();
        inventoryMovementsRepository.save(m);
    }
}
