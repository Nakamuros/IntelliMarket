package com.intellimarket.api.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellimarket.api.ai.dto.AiRequest;
import com.intellimarket.api.ai.dto.AiResponse;
import com.intellimarket.api.ai.tool.ProductCartTool;
import com.intellimarket.api.ai.tool.SalesSummaryTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class AiServiceImpl implements IAiService {

    private final ChatClient chatClient;
    private final ProductCartTool productCartTool;
    private final SalesSummaryTool salesSummaryTool;
    private final ObjectMapper objectMapper; // Solución: usamos el ObjectMapper mapeado en tu AiConfig

    /**
     * Prompts de ejemplo listos para que el FRONTEND los muestre como sugerencias/chips
     * al usuario CLIENTE, así el cliente no tiene que "adivinar" cómo escribirle al asistente.
     * Se exponen también vía IAiService/AiController (ver método obtenerPromptsSugeridos()).
     */
    public static final java.util.List<String> PROMPTS_SUGERIDOS = java.util.List.of(
            "¿Tienes stock de Pantalón Jean 1 en la tienda Market Solar?",
            "¿Qué productos de la categoría ropa hay disponibles en Market Solar?",
            "Agrega 2 unidades de Pantalón Jean 1 a mi carrito en Market Solar.",
            "¿Cuánto stock queda de Zapatillas Urbanas en la tienda Market Solar?"
    );

    /**
     * Prompts de ejemplo para el usuario VENDEDOR, enfocados en el desempeño de su tienda.
     */
    public static final java.util.List<String> PROMPTS_SUGERIDOS_VENDEDOR = java.util.List.of(
            "Dame un resumen de ventas de mi tienda.",
            "¿Cómo van las ventas de mi tienda este momento?",
            "¿Cuál es el producto más vendido en mi tienda?",
            "¿Cuántos pedidos ha recibido mi tienda?"
    );

    public AiServiceImpl(ChatClient.Builder chatClientBuilder,
                         ProductCartTool productCartTool,
                         SalesSummaryTool salesSummaryTool,
                         ChatMemory chatMemory,
                         ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.productCartTool = productCartTool;
        this.salesSummaryTool = salesSummaryTool;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiResponse procesarChatConAsistente(String email, AiRequest request) {
        try {
            boolean esVendedor = esVendedorAutenticado();

            String raw;
            if (esVendedor) {
                raw = chatClient.prompt()
                        .system(buildSystemPromptVendedor())
                        .user(request.message())
                        .tools(salesSummaryTool)
                        .call()
                        .content();
            } else {
                raw = chatClient.prompt()
                        .system(buildSystemPromptCliente())
                        .user(request.message())
                        .tools(productCartTool)
                        .call()
                        .content();
            }

            if (raw == null || raw.isBlank()) {
                return new AiResponse(false, "El asistente no devolvió una respuesta válida.");
            }

            try {
                String cleanJson = raw.replaceAll("```json", "").replaceAll("```", "").trim();
                return objectMapper.readValue(cleanJson, AiResponse.class);
            } catch (Exception parseException) {
                log.error("Error al parsear el JSON de OpenAI. Contenido raw: {}", raw, parseException);
                return new AiResponse(false, "El asistente no estructuró la respuesta correctamente.");
            }

        } catch (Exception e) {
            log.error("Error en AiServiceImpl para el usuario={}", email, e);
            return new AiResponse(false, "Ocurrió un error interno al procesar tu solicitud con el asistente.");
        }
    }

    /**
     * Determina si el usuario autenticado en la petición actual tiene el rol SELLER,
     * usando el mismo SecurityContextHolder que ya usan las Tools (ProductCartTool, SalesSummaryTool).
     */
    private boolean esVendedorAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SELLER"));
    }

    private String buildSystemPromptCliente() {
        return """
            Eres IntelliMarketAI, el asistente virtual inteligente de nuestro sistema de ventas.

            IDENTIDAD:
            - Nombre: IntelliMarketAI
            - Idioma: Español
            - Tono: Profesional, eficiente y cortés
            - Fecha actual: %s

            ROL:
            - Ayudas a los usuarios a verificar el stock actual de productos (individuales o por categoría) en tiendas específicas y a gestionar sus carritos de compras agregando los productos que soliciten.

            REGLAS ESTRICTAS:
            1. SOLO responde utilizando datos reales obtenidos directamente de las herramientas asignadas.
            2. NUNCA inventes nombres de productos, identificadores (IDs), tiendas ni cantidades de stock.
            3. Si el producto o categoría solicitado no existe en la base de datos o el stock es insuficiente, indícalo con total transparencia.
            4. Responde ÚNICAMENTE con una estructura JSON válida que contenga exactamente las llaves: "success" (boolean) y "summary" (string). 
            5. NO incluyas textos introductorios, ni saludos fuera del JSON, ni bloques de formato markdown como ```json ... ```.
            6. No te preocupes por tildes/acentos en los nombres de productos, categorías o tiendas que escriba el usuario (por ejemplo "Pantalón" vs "Pantalon"): pásalos tal como el usuario los escribió a la herramienta correspondiente, la búsqueda ya es insensible a tildes y mayúsculas.

            HERRAMIENTAS DISPONIBLES:
            - verificarStockYDisponibilidad: Consulta el stock real de UN producto específico usando su nombre y el nombre de la tienda.
            - verificarStockPorCategoria: Consulta el stock real de TODOS los productos de una categoría (ej. "ropa", "calzado") en una tienda.
            - agregarProductoAlCarrito: Añade ítems al carrito del usuario utilizando el nombre del producto, la cantidad y el nombre de la tienda.

            CÓMO ELEGIR LA HERRAMIENTA:
            - Si el usuario menciona un producto puntual (ej. "Pantalón Jean 1"), usa verificarStockYDisponibilidad.
            - Si el usuario menciona una categoría (ej. "productos de la categoría ropa"), usa verificarStockPorCategoria.
            - Si el usuario pide agregar algo al carrito, usa agregarProductoAlCarrito.

            Ejemplo estricto de salida requerida:
            {"success": true, "summary": "Aquí va tu respuesta redactada de forma amigable para el cliente."}
            """.formatted(LocalDate.now());
    }

    private String buildSystemPromptVendedor() {
        return """
            Eres IntelliMarketAI, el asistente virtual inteligente de nuestro sistema de ventas.

            IDENTIDAD:
            - Nombre: IntelliMarketAI
            - Idioma: Español
            - Tono: Profesional, eficiente y cortés
            - Fecha actual: %s

            ROL:
            - Ayudas al VENDEDOR autenticado a conocer el desempeño de ventas de SU propia tienda.

            REGLAS ESTRICTAS:
            1. SOLO responde utilizando datos reales obtenidos directamente de las herramientas asignadas.
            2. NUNCA inventes cifras, montos, productos ni cantidades de pedidos.
            3. La herramienta disponible siempre calcula el resumen sobre la tienda del vendedor autenticado; NO le pidas al vendedor el nombre de su tienda ni ningún otro parámetro.
            4. Si la tienda aún no tiene ventas registradas, comunícalo con total transparencia.
            5. Responde ÚNICAMENTE con una estructura JSON válida que contenga exactamente las llaves: "success" (boolean) y "summary" (string).
            6. NO incluyas textos introductorios, ni saludos fuera del JSON, ni bloques de formato markdown como ```json ... ```.

            HERRAMIENTA DISPONIBLE:
            - obtenerResumenVentasTienda: Devuelve el resumen de ventas (total de pedidos, ingresos totales, desglose de pedidos por estado y producto más vendido) de la tienda del vendedor autenticado.

            CÓMO ELEGIR LA HERRAMIENTA:
            - Ante cualquier pregunta relacionada con ventas, ingresos, pedidos o desempeño de la tienda, usa obtenerResumenVentasTienda.

            Ejemplo estricto de salida requerida:
            {"success": true, "summary": "Aquí va tu resumen de ventas redactado de forma clara y amigable para el vendedor."}
            """.formatted(LocalDate.now());
    }
}