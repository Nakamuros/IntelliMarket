package com.intellimarket.api.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
public class AiConfig {

    /**
     * Implementación segura y universal de ChatMemory.
     * No depende de ninguna clase interna de Spring AI que cambie entre versiones,
     * garantizando que compile en cualquier máquina.
     */
    @Bean
    public ChatMemory chatMemory() {
        return new ChatMemory() {
            private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

            @Override
            public void add(String conversationId, List<Message> messages) {
                if (conversationId != null && messages != null) {
                    conversationHistory.computeIfAbsent(conversationId, k -> new CopyOnWriteArrayList<>())
                            .addAll(messages);
                }
            }

            // CORREGIDO: En tu versión, este método solo recibe el ID de la conversación
            @Override
            public List<Message> get(String conversationId) {
                if (conversationId == null) {
                    return List.of();
                }
                return conversationHistory.getOrDefault(conversationId, List.of());
            }

            @Override
            public void clear(String conversationId) {
                if (conversationId != null) {
                    conversationHistory.remove(conversationId);
                }
            }
        };
    }

    /**
     * Solución al conflicto de Jackson:
     * Al marcar este ObjectMapper como @Primary, le indicamos a Spring Boot que use
     * esta configuración para todos los controladores normales de tu aplicación,
     * evitando que las configuraciones internas de Spring AI interfieran o rompan tus DTOs.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Soporte correcto para fechas (LocalDateTime, LocalDate) en tus entidades y DTOs
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}