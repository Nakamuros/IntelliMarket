package com.intellimarket.api.ai.tool;

import com.intellimarket.api.inventory.service.IInventoryService;
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

    @Tool(description = "Verifica el stock y disponibilidad de un producto específico en una tienda usando el nombre del producto y el ID de la tienda.")
    public String verificarStockYDisponibilidad(String nombreProducto, Long storeId) {
        try {
            var productos = inventoryService.getStockByStore(storeId);

            if (productos == null || productos.isEmpty()) {
                return "No se encontraron productos registrados en la tienda con ID " + storeId + ".";
            }

            // Buscamos dinámicamente en la lista que devuelva el servicio real
            var productoEncontrado = productos.stream()
                    .filter(p -> p.name() != null && p.name().toLowerCase().contains(nombreProducto.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            if (productoEncontrado == null) {
                return "El producto '" + nombreProducto + "' no está disponible actualmente en esta tienda.";
            }

            return String.format("Product: %s | ID: %d | Stock available: %d units.",
                    productoEncontrado.name(), productoEncontrado.id(), productoEncontrado.stock());

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