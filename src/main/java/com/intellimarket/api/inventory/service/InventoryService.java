package com.intellimarket.api.inventory.service;

import com.intellimarket.api.inventory.dto.ProductsRequest;
import com.intellimarket.api.inventory.dto.ProductsResponse;
import com.intellimarket.api.inventory.mapper.ProductsMapper;
import com.intellimarket.api.inventory.model.*;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.inventory.repository.InventoryMovementRepository;
import com.intellimarket.api.product.model.Product;
import com.intellimarket.api.product.repository.ProductRepository;
import com.intellimarket.api.shared.exception.BusinessRuleException;
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
    // Crear prodducto
    public ProductsResponse createProduct(Long store_id, ProductsRequest request) {
        // 1. Guardar el producto en su catálogo (Products)
        // Catálogo global
        Product product = productRepository.save(Product.builder()
                .name(request.name())
                .category(request.category())
                .description(request.description())
                .unitPrice(request.unitPrice()) // Assuming price maps to unitPrice in Product
                //.stock(request.stock())
                //.status(request.stock() > 0 ? 1 : 0)
                .build());

        Store store = storeRepository.findById(store_id)
                .orElseThrow(() -> new ResourceNotFoundException("Tienda no encontrada"));

        // Guardar producto en bodega de tienda o su inventario (Inventory)
        Inventory inventory = inventoryRepository.save(Inventory.builder().product(product)
                .store(store)
                .stock(request.stock()) // El stock inicial va aquí
                .price(request.unitPrice())
                .state(request.stock() > 0 ? 1 : 0)
                .build());

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
        Product product = productRepository.findById(product_id).orElseThrow(()-> new
                ResourceNotFoundException("Producto no existe"));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(product_id, store_id).
                orElseThrow(()-> new ResourceNotFoundException("Producto no asignado a esta tienda"));

        // Actualizamos según la US-06
        product.setDescription(request.description());
        product.setName(request.name());
        inventory.setStock(request.stock());
        product.setCategory(request.category());
        product.setUnitPrice(request.unitPrice());

        productRepository.save(product);
        inventoryRepository.save(inventory);

        return productsMapper.toResponse(product, inventory);
    }

    @Override
    // Stock de productos por tienda
    public List<ProductsResponse> getStockByStore(Long store_id){
        // US-07
        return inventoryRepository.findByStoreIdAndState(store_id, 1).stream().map(inv->productsMapper.
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

    //@Override
    // US-09: El estado cambia

    @Override
    @Transactional(readOnly = true)
    // US-10: Obtener un producto específico con su stock de tienda
    public ProductsResponse getProductByIdAndStore(Long productId, Long storeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto solicitado no existe"));

        Inventory inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("El producto no está registrado en esta tienda"));

        return productsMapper.toResponse(product, inventory);
    }


    private void saveMovement(Product p, Store sId, Integer qty, Type type, Reference_Type ref) {
        InventoryMovement m = InventoryMovement.builder()
                .product(p)
                .store(sId)
                .quantity(qty)
                .type(type)
                .reference_type(ref)
                .build();
        inventoryMovementRepository.save(m);
    }
}
