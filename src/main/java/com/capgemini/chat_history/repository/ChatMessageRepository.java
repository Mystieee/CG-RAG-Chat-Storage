package com.capgemini.chat_history.repository;

import com.capgemini.chat_history.entity.ChatMessage;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    Page<ChatMessage> findBySessionIdOrderByCreatedAtAsc(UUID sessionId, Pageable pageable);

    Page<ChatMessage> findBySessionId(UUID sessionId, Pageable pageable);
}
