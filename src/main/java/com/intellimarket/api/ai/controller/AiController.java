package com.intellimarket.api.ai.controller;

import com.intellimarket.api.ai.dto.AiRequest;
import com.intellimarket.api.ai.dto.AiResponse;
import com.intellimarket.api.ai.service.IAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final IAiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<AiResponse> chatearConAsistente(@Valid @RequestBody AiRequest request) {
        // Por ahora pasamos un email "anonimo" o "test@tienda.com" ya que la ruta es pública temporalmente.
        // Si luego activamos la seguridad con token, aquí se usará (Authentication authentication) igual que en tu OrderController.
        String emailSimulado = "test@intellimarket.com";

        AiResponse response = aiService.procesarChatConAsistente(emailSimulado, request);

        return ResponseEntity.ok(response);
    }
}