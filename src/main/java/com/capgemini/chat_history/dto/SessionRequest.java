package com.capgemini.chat_history.dto;

import jakarta.validation.constraints.NotBlank;

public record SessionRequest(
        @NotBlank String userId,
        String title,
        Boolean isFavorite
)  {}
