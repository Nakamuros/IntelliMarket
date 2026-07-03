package com.intellimarket.api.ai.tool;

import com.intellimarket.api.order.model.Order;
import com.intellimarket.api.order.model.OrderItem;
import com.intellimarket.api.order.repository.OrderRepository;
import com.intellimarket.api.store.model.Store;
import com.intellimarket.api.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class SalesSummaryTool {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    @Tool(description = "Obtiene el resumen de ventas (total de pedidos, ingresos totales, desglose de pedidos por estado y producto más vendido) de la tienda que pertenece al vendedor actualmente autenticado. No recibe parámetros: siempre calcula el resumen sobre la tienda del vendedor en sesión.")
    public String obtenerResumenVentasTienda() {
        String emailVendedor = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            Optional<Store> tiendaOpt = storeRepository.findByOwnerEmail(emailVendedor);
            if (tiendaOpt.isEmpty()) {
                return "No se encontró ninguna tienda asociada al vendedor autenticado.";
            }

            Store tienda = tiendaOpt.get();
            List<Order> pedidos = orderRepository.findByStoreIdWithItems(tienda.getId());

            if (pedidos.isEmpty()) {
                return "La tienda '" + tienda.getName() + "' aún no registra ventas.";
            }

            int totalPedidos = pedidos.size();
            BigDecimal ingresoTotal = BigDecimal.ZERO;
            Map<String, Integer> pedidosPorEstado = new TreeMap<>();
            Map<String, Integer> unidadesPorProducto = new HashMap<>();

            for (Order pedido : pedidos) {
                ingresoTotal = ingresoTotal.add(pedido.getTotalAmount());
                pedidosPorEstado.merge(pedido.getStatus(), 1, Integer::sum);

                for (OrderItem item : pedido.getItems()) {
                    String nombreProducto = item.getProduct().getName();
                    unidadesPorProducto.merge(nombreProducto, item.getQuantity(), Integer::sum);
                }
            }

            String productoMasVendido = unidadesPorProducto.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + " (" + e.getValue() + " unidades vendidas)")
                    .orElse("Sin datos suficientes de productos.");

            StringBuilder resultado = new StringBuilder();
            resultado.append("Resumen de ventas de la tienda '").append(tienda.getName()).append("':\n");
            resultado.append("Total de pedidos registrados: ").append(totalPedidos).append("\n");
            resultado.append("Ingresos totales: ").append(ingresoTotal).append("\n");
            resultado.append("Producto más vendido: ").append(productoMasVendido).append("\n");
            resultado.append("Pedidos por estado:\n");
            for (Map.Entry<String, Integer> entry : pedidosPorEstado.entrySet()) {
                resultado.append(" - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" pedido(s)\n");
            }

            return resultado.toString();

        } catch (Exception e) {
            return "No se pudo calcular el resumen de ventas en este momento debido a un error interno.";
        }
    }
}