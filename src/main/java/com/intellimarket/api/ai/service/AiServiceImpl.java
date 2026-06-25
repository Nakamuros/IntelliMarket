package com.intellimarket.api.ai.service;

import com.intellimarket.api.ai.dto.AiRequest;
import com.intellimarket.api.ai.dto.AiResponse;
import com.intellimarket.api.ai.tool.ProductCartTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements IAiService {

    private final ProductCartTool productCartTool;

    @Override
    public AiResponse procesarChatConAsistente(String email, AiRequest request) {
        String mensajeMin = request.message().toLowerCase();
        String respuestaTexto;

        // 1. INTENCIÓN: Consulta de Stock Real
        if (mensajeMin.contains("stock") || mensajeMin.contains("available") || mensajeMin.contains("have") || mensajeMin.contains("check")) {

            String productoBuscado = "";
            Long storeId = 1L;

            // Mapeamos rigurosamente la tienda según tu pgAdmin
            if (mensajeMin.contains("volt")) {
                productoBuscado = "Volt energizante"; // Nombre exacto en tu BD
                storeId = 1L;
            } else if (mensajeMin.contains("coke")) {
                productoBuscado = "Coca-cola"; // Nombre exacto en tu BD
                storeId = 2L;
            } else if (mensajeMin.contains("cable")) {
                productoBuscado = "Cable USB-C"; // Nombre exacto en tu BD
                storeId = 2L;
            } else if (mensajeMin.contains("sneakers") || mensajeMin.contains("nike")) {
                productoBuscado = "Zapatillas Nike"; // Nombre exacto en tu BD
                storeId = 3L;
            } else if (mensajeMin.contains("yogurt") || mensajeMin.contains("gloria")) {
                productoBuscado = "Yogurt Gloria"; // Nombre exacto en tu BD
                storeId = 3L;
            }

            if (!productoBuscado.isEmpty()) {
                // LLAMADA 100% REAL A TU HERRAMIENTA DE BASE DE DATOS
                respuestaTexto = productCartTool.verificarStockYDisponibilidad(productoBuscado, storeId);
            } else {
                respuestaTexto = "I'm sorry, I couldn't identify the product you are looking for in our stores.";
            }

            // 2. INTENCIÓN: Agregar al Carrito (Usa tu Tool Real)
        } else if (mensajeMin.contains("cart") || mensajeMin.contains("add")) {
            Long idProducto = 1L;
            Long idTienda = 1L;

            // Identificamos qué producto quiere añadir al carrito para mandarle el ID correcto
            if (mensajeMin.contains("coke") || mensajeMin.contains("coca")) {
                idProducto = 2L; idTienda = 2L;
            } else if (mensajeMin.contains("cable")) {
                idProducto = 3L; idTienda = 2L;
            } else if (mensajeMin.contains("sneakers") || mensajeMin.contains("nike")) {
                idProducto = 4L; idTienda = 3L;
            } else if (mensajeMin.contains("yogurt") || mensajeMin.contains("gloria")) {
                idProducto = 5L; idTienda = 3L;
            } else if (mensajeMin.contains("volt")) {
                idProducto = 1L; idTienda = 1L; // Si este da error, confirma con tus compañeros qué ID tiene el Volt en su tabla 'products'
            }

            // Llamamos a tu herramienta pasándole los IDs correspondientes
            respuestaTexto = productCartTool.agregarProductoAlCarrito(idProducto, 1, idTienda);
        } else {
            respuestaTexto = "Hello, I’m IntelliMarket's assistant. Would you like to check the stock of a product or add something to your cart?";
        }

        return new AiResponse(respuestaTexto);
    }
}