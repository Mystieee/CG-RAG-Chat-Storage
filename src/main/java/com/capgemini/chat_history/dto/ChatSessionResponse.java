package com.capgemini.chat_history.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatSessionResponse(
        UUID id,
        String userId,
        String title,
        boolean isFavorite,
        LocalDateTime createdAt
) {
}
