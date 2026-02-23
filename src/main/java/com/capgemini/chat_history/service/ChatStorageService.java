package com.capgemini.chat_history.service;

import com.capgemini.chat_history.dto.ChatHistoryResponse;
import com.capgemini.chat_history.dto.ChatSessionResponse;
import com.capgemini.chat_history.dto.MessageRequest;
import com.capgemini.chat_history.dto.SessionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChatStorageService {

    ChatSessionResponse createSession(SessionRequest request);
    ChatSessionResponse updateSessionMetadata(UUID id, SessionRequest request);
    void deleteSession(UUID id);
    ChatHistoryResponse addMessage(UUID sessionId, MessageRequest request);
    Page<ChatHistoryResponse> getSessionHistory(UUID sessionId, Pageable pageable);




}
