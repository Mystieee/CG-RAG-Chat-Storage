package com.capgemini.chat_history.dto;

import com.capgemini.chat_history.entity.MessageRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatHistoryResponse(
        UUID id,
        MessageRole role,
        String content,
        String retrievedContext,
        UUID sessionId, // Just return the ID, not the whole object
        LocalDateTime createdAt
) {}
