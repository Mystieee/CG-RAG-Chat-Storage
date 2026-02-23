package com.capgemini.chat_history.dto;

import com.capgemini.chat_history.entity.MessageRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageRequest (
        @NotNull MessageRole role,
        @NotBlank String content,
        String context
){}
