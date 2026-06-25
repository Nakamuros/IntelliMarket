package com.intellimarket.api.ai.service;

import com.intellimarket.api.ai.dto.AiRequest;
import com.intellimarket.api.ai.dto.AiResponse;

public interface IAiService {
    AiResponse procesarChatConAsistente(String email, AiRequest request);
}