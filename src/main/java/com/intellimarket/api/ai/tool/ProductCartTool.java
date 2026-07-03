package com.intellimarket.api.ai.tool;

import com.intellimarket.api.inventory.service.IInventoryService;
import com.intellimarket.api.store.repository.StoreRepository;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.inventory.model.Inventory;
import com.intellimarket.api.order.service.IOrderService;
import com.intellimarket.api.order.dto.AddToCartRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCartTool {

    private final IInventoryService inventoryService;
    private final IOrderService orderService;
    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Tool(description = "Verifica el stock y disponibilidad de un producto específico en una tienda usando el nombre del producto y el nombre de la tienda.")
    public String verificarStockYDisponibilidad(String nombreProducto, String nombreTienda) {
        try {
            var tiendaOpt = storeRepository.findByNameIgnoreCase(nombreTienda);
            if (tiendaOpt.isEmpty()) {
                return "No se encontró ninguna tienda con el nombre '" + nombreTienda + "'.";
            }

            Long storeId = tiendaOpt.get().getId();
            var inventarioOpt = buscarInventarioPorNombre(nombreProducto, storeId);

            if (inventarioOpt.isEmpty()) {
                return "El producto '" + nombreProducto + "' no está disponible actualmente en esta tienda.";
            }

            var inventario = inventarioOpt.get();
            return String.format("Product: %s | ID: %d | Stock available: %d units.",
                    inventario.getProduct().getName(),
                    inventario.getProduct().getId(),
                    inventario.getStock());

        } catch (Exception e) {
            return "No se pudo verificar el stock en este momento debido a un error interno.";
        }
    }

    @Tool(description = "Agrega un producto al carrito de compras usando el nombre del producto, la cantidad solicitada y el nombre de la tienda.")
    public String agregarProductoAlCarrito(String nombreProducto, int cantidad, String nombreTienda) {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            var tiendaOpt = storeRepository.findByNameIgnoreCase(nombreTienda);
            if (tiendaOpt.isEmpty()) {
                return "No se encontró ninguna tienda con el nombre '" + nombreTienda + "'.";
            }

            Long storeId = tiendaOpt.get().getId();
            var inventarioOpt = buscarInventarioPorNombre(nombreProducto, storeId);

            if (inventarioOpt.isEmpty()) {
                return "El producto '" + nombreProducto + "' no está disponible actualmente en esta tienda.";
            }

            var inventario = inventarioOpt.get();

            if (inventario.getStock() < cantidad) {
                return "No hay stock suficiente de '" + nombreProducto + "'. Stock disponible: " + inventario.getStock() + " unidades.";
            }

            Long productoId = inventario.getProduct().getId();

            AddToCartRequestDTO request = new AddToCartRequestDTO(null, productoId, storeId, cantidad);
            orderService.addItemToCartByEmail(emailUsuario, request);

            return "Éxito: Se han agregado " + cantidad + " unidad(es) de '" + inventario.getProduct().getName() + "' al carrito.";

        } catch (Exception e) {
            return "Error al intentar agregar el producto al carrito: " + e.getMessage();
        }
    }

    private Optional<Inventory> buscarInventarioPorNombre(String nombreProducto, Long storeId) {
        return inventoryRepository.findByProductNameAndStoreId(nombreProducto, storeId);
    }
}