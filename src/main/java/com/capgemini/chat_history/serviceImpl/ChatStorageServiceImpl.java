package com.capgemini.chat_history.serviceImpl;

import com.capgemini.chat_history.dto.ChatHistoryResponse;
import com.capgemini.chat_history.dto.ChatSessionResponse;
import com.capgemini.chat_history.dto.MessageRequest;
import com.capgemini.chat_history.dto.SessionRequest;
import com.capgemini.chat_history.entity.ChatMessage;
import com.capgemini.chat_history.entity.ChatSession;
import com.capgemini.chat_history.repository.ChatMessageRepository;
import com.capgemini.chat_history.repository.ChatSessionRepository;
import com.capgemini.chat_history.service.ChatStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChatStorageServiceImpl implements ChatStorageService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Override
    @Transactional
    public ChatSessionResponse createSession(SessionRequest request) {
        ChatSession session = ChatSession.builder()
                .userId(request.userId())
                .title(request.title())
                .isFavorite(false)
                .build();
        return mapToSessionDto(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public ChatSessionResponse updateSessionMetadata(UUID id, SessionRequest request) {
        ChatSession session = findSessionOrThrow(id);
        if (request.title() != null) session.setTitle(request.title());
        if (request.isFavorite() != null) session.setFavorite(request.isFavorite());
        return mapToSessionDto(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public void deleteSession(UUID id) {
        if (!sessionRepository.existsById(id)) throw new EntityNotFoundException("Session not found");
        sessionRepository.deleteById(id);
        log.info("Session {} deleted successfully", id);
    }

    @Override
    @Transactional
    public ChatHistoryResponse addMessage(UUID sessionId, MessageRequest request) {
        ChatSession session = findSessionOrThrow(sessionId);
        ChatMessage message = ChatMessage.builder()
                .role(request.role())
                .content(request.content())
                .session(session)
                .build();
        return mapToMessageDto(messageRepository.save(message));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatHistoryResponse> getSessionHistory(UUID sessionId, Pageable pageable) {
        return messageRepository.findBySessionId(sessionId, pageable)
                .map(this::mapToMessageDto);
    }

    private ChatSession findSessionOrThrow(UUID id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + id));
    }

    private ChatSessionResponse mapToSessionDto(ChatSession s) {
        return new ChatSessionResponse(s.getId(), s.getUserId(), s.getTitle(), s.isFavorite(), s.getCreatedAt());
    }

    private ChatHistoryResponse mapToMessageDto(ChatMessage m) {
        return new ChatHistoryResponse(m.getId(), m.getRole(), m.getContent(),
                m.getRetrievedContext(), m.getSession().getId(), m.getCreatedAt());
    }
}
