package com.intellimarket.api.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellimarket.api.ai.dto.AiRequest;
import com.intellimarket.api.ai.dto.AiResponse;
import com.intellimarket.api.ai.tool.ProductCartTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class AiServiceImpl implements IAiService {

    private final ChatClient chatClient;
    private final ProductCartTool productCartTool;
    private final ObjectMapper objectMapper; // Solución: usamos el ObjectMapper mapeado en tu AiConfig

    public AiServiceImpl(ChatClient.Builder chatClientBuilder,
                         ProductCartTool productCartTool,
                         ChatMemory chatMemory,
                         ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.productCartTool = productCartTool;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiResponse procesarChatConAsistente(String email, AiRequest request) {
        try {
            String raw = chatClient.prompt()
                    .system(buildSystemPrompt())
                    .user(request.message())
                    .tools(productCartTool)
                    .call()
                    .content();

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

    private String buildSystemPrompt() {
        return """
            Eres IntelliMarketAI, el asistente virtual inteligente de nuestro sistema de ventas.

            IDENTIDAD:
            - Nombre: IntelliMarketAI
            - Idioma: Español
            - Tono: Profesional, eficiente y cortés
            - Fecha actual: %s

            ROL:
            - Ayudas a los usuarios a verificar el stock actual de productos en tiendas específicas y a gestionar sus carritos de compras agregando los productos que soliciten.

            REGLAS ESTRICTAS:
            1. SOLO responde utilizando datos reales obtenidos directamente de las herramientas asignadas.
            2. NUNCA inventes nombres de productos, identificadores (IDs), tiendas ni cantidades de stock.
            3. Si el producto solicitado no existe en la base de datos o el stock es insuficiente, indícalo con total transparencia.
            4. Responde ÚNICAMENTE con una estructura JSON válida que contenga exactamente las llaves: "success" (boolean) y "summary" (string). 
            5. NO incluyas textos introductorios, ni saludos fuera del JSON, ni bloques de formato markdown como ```json ... ```.

            HERRAMIENTAS DISPONIBLES:
            - verificarStockYDisponibilidad: Consulta el stock real de un producto usando su nombre y el nombre de la tienda.
            - agregarProductoAlCarrito: Añade ítems al carrito del usuario utilizando el nombre del producto, la cantidad y el nombre de la tienda.

            Ejemplo estricto de salida requerida:
            {"success": true, "summary": "Aquí va tu respuesta redactada de forma amigable para el cliente."}
            """.formatted(LocalDate.now());
    }
}