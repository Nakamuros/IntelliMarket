package com.intellimarket.api.ai.controller;

import com.intellimarket.api.ai.dto.AiRequest;
import com.intellimarket.api.ai.dto.AiResponse;
import com.intellimarket.api.ai.service.AiServiceImpl;
import com.intellimarket.api.ai.service.IAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final IAiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<AiResponse> chatearConAsistente(Principal principal,@Valid @RequestBody AiRequest request) {
        // Por ahora pasamos un email "anonimo" o "test@tienda.com" ya que la ruta es pública temporalmente.
        // Si luego activamos la seguridad con token, aquí se usará (Authentication authentication) igual que en tu OrderController.
        String emailReal = principal.getName();

        AiResponse response = aiService.procesarChatConAsistente(emailReal, request);

        return ResponseEntity.ok(response);
    }

    // Endpoint pensado para que el FRONTEND pinte "sugerencias" (chips/botones)
    // con ejemplos de mensajes, ya que el usuario final no usará Postman.
    // Devuelve sugerencias distintas según el rol del usuario autenticado (CUSTOMER o SELLER).
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> obtenerPromptsSugeridos() {
        boolean esVendedor = esVendedorAutenticado();

        return ResponseEntity.ok(esVendedor
                ? AiServiceImpl.PROMPTS_SUGERIDOS_VENDEDOR
                : AiServiceImpl.PROMPTS_SUGERIDOS);
    }

    private boolean esVendedorAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SELLER"));
    }
}