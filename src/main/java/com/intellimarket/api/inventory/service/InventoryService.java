package com.intellimarket.api.inventory.service;

import com.intellimarket.api.inventory.dto.ProductsRequest;
import com.intellimarket.api.inventory.dto.ProductsResponse;
import com.intellimarket.api.inventory.mapper.ProductsMapper;
import com.intellimarket.api.inventory.model.*;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.inventory.repository.Inventory_MovementsRepository;
import com.intellimarket.api.inventory.repository.ProductsRepository;
import com.intellimarket.api.shared.exception.BusinessRuleException;
import com.intellimarket.api.shared.exception.ResourceNotFoundException;
import com.intellimarket.api.stores.model.Stores;
import com.intellimarket.api.stores.repository.StoresRepository;
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
    private final StoresRepository storesRepository;

    @Override
    @Transactional
    // Crear prodducto
    public ProductsResponse createProduct(Long store_id, ProductsRequest request) {
        // 1. Guardar el producto en su catálogo (Products)
        // Catálogo global
        Products product = productsRepository.save(Products.builder().name(request.name())
        .category(request.category()).description(request.description()).build());

        Stores store = storesRepository.findById(store_id)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        // Guardar producto en bodega de tienda o su inventario (Inventory)
        Inventory inventory = inventoryRepository.save(Inventory.builder().product(product).
                store(store).stock(request.stock()).price(request.price()).build());

        // Registrar movimiento de historial en Inventory_Movements
        // Tipo de movimiento: Compra
        // Referencia de tipo: Proveedor, compra a proveedor de producto, producto agregado
        saveMovement(product, store, request.stock(),
                Type.PURCHASE, Reference_Type.PROVIDER);

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
        List<Inventory> criticalItems = inventoryRepository.findByStoreIdAndStockLessThan(store_id, 10);

        // Eliminamos el throw para no generar un 404 innecesario.
        // El Controller ya maneja el mensaje si la lista viene vacía.
        return criticalItems.stream()
                .map(inv -> productsMapper.toResponse(inv.getProduct(), inv))
                .toList();
    }

    @Override
    @Transactional
    // US-09: ELiminar producto
    public void deleteProduct(Long product_id, Long store_id){
        Stores store = storesRepository.findById(store_id)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(product_id, store_id)
                .orElseThrow(()-> new ResourceNotFoundException("No se encontró el registro en el inventario"));

        if (inventory.getStock() != 0) {
            throw new BusinessRuleException("No se puede eliminar un producto que aún tiene " + inventory.getStock() + " unidades en stock.");
        }

        // 1. REGISTRAR MOVIMIENTO ANTES DE BORRAR
        saveMovement(inventory.getProduct(), store, 0, Type.ADJUSTMENT, Reference_Type.ADJUSTMENT);

        // 2. AHORA SÍ BORRAMOS
        inventoryRepository.delete(inventory);
    }

    private void saveMovement(Products p, Stores sId, Integer qty, Type type, Reference_Type ref) {
        Inventory_Movements m = Inventory_Movements.builder()
                .product(p)
                .store(sId)
                .quantity(qty)
                .type(type)
                .reference_type(ref)
                .build();
        inventoryMovementsRepository.save(m);
    }
}
