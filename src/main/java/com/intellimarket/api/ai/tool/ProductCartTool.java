package com.intellimarket.api.ai.tool;

import com.intellimarket.api.inventory.service.IInventoryService;
import com.intellimarket.api.inventory.repository.InventoryRepository;
import com.intellimarket.api.order.service.IOrderService;
import com.intellimarket.api.order.dto.AddToCartRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCartTool {

    private final IInventoryService inventoryService;
    private final IOrderService orderService;
    private final InventoryRepository inventoryRepository;

    @Tool(description = "Verifica el stock y disponibilidad de un producto específico en una tienda usando el nombre del producto y el ID de la tienda.")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String verificarStockYDisponibilidad(String nombreProducto, Long storeId) {
        try {
            var inventarioOpt = inventoryRepository.findByProductNameAndStoreId(nombreProducto, storeId);

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

    @Tool(description = "Agrega un producto al carrito de compras usando el ID del producto, la cantidad solicitada y el ID de la tienda.")
    public String agregarProductoAlCarrito(Long productoId, int cantidad, Long storeId) {
        // Obtenemos el usuario autenticado dinámicamente del contexto de seguridad
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            AddToCartRequestDTO request = new AddToCartRequestDTO(null, productoId, storeId, cantidad);
            orderService.addItemToCartByEmail(emailUsuario, request);
            return "Éxito: Se han agregado " + cantidad + " unidad(es) del producto con ID " + productoId + " al carrito.";
        } catch (Exception e) {
            return "Error al intentar agregar el producto al carrito: " + e.getMessage();
        }
    }
}