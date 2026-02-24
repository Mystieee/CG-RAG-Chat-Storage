package com.capgemini.chat_history.serviceImpl;

import com.capgemini.chat_history.dto.ChatHistoryResponse;
import com.capgemini.chat_history.dto.ChatSessionResponse;
import com.capgemini.chat_history.dto.MessageRequest;
import com.capgemini.chat_history.dto.SessionRequest;
import com.capgemini.chat_history.entity.ChatMessage;
import com.capgemini.chat_history.entity.ChatSession;
import com.capgemini.chat_history.entity.MessageRole;
import com.capgemini.chat_history.repository.ChatMessageRepository;
import com.capgemini.chat_history.repository.ChatSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatStorageServiceImplTest {
    @Mock
    private ChatSessionRepository sessionRepository;

    @Mock
    private ChatMessageRepository messageRepository;

    @InjectMocks
    private ChatStorageServiceImpl chatService;

    private UUID sessionId;
    private ChatSession mockSession;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        now = LocalDateTime.now();
        mockSession = ChatSession.builder()
                .id(sessionId)
                .userId("user-1")
                .title("Initial Title")
                .isFavorite(false)
                .createdAt(now)
                .build();
    }

    // --- createSession Tests ---
    @Test
    void createSession_ShouldReturnDto() {
        SessionRequest request = new SessionRequest("user-1", "New Session", false);
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(mockSession);

        ChatSessionResponse result = chatService.createSession(request);

        assertNotNull(result);
        assertEquals(sessionId, result.id());
        verify(sessionRepository, times(1)).save(any());
    }

    // --- updateSessionMetadata Tests ---
    @Test
    void updateSessionMetadata_ShouldUpdateAllFields_WhenRequestIsFull() {
        SessionRequest request = new SessionRequest(null, "Updated Title", true);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(mockSession);

        chatService.updateSessionMetadata(sessionId, request);

        assertEquals("Updated Title", mockSession.getTitle());
        assertTrue(mockSession.isFavorite());
    }

    @Test
    void updateSessionMetadata_ShouldNotUpdate_WhenFieldsAreNull() {
        // request.title() is null, request.isFavorite() is null
        SessionRequest request = new SessionRequest(null, null, null);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(mockSession);

        chatService.updateSessionMetadata(sessionId, request);

        assertEquals("Initial Title", mockSession.getTitle());
        assertFalse(mockSession.isFavorite());
    }

    // --- deleteSession Tests ---
    @Test
    void deleteSession_ShouldDelete_WhenExists() {
        when(sessionRepository.existsById(sessionId)).thenReturn(true);

        assertDoesNotThrow(() -> chatService.deleteSession(sessionId));
        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    void deleteSession_ShouldThrow_WhenNotExists() {
        when(sessionRepository.existsById(sessionId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> chatService.deleteSession(sessionId));
    }

    // --- addMessage Tests ---
    @Test
    void addMessage_ShouldReturnDto() {
        MessageRequest request = new MessageRequest(MessageRole.USER, "Hello", null);
        ChatMessage mockMessage = ChatMessage.builder()
                .id(UUID.randomUUID())
                .role(MessageRole.USER)
                .content("Hello")
                .session(mockSession)
                .createdAt(now)
                .build();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(mockMessage);

        ChatHistoryResponse result = chatService.addMessage(sessionId, request);

        assertNotNull(result);
        assertEquals("Hello", result.content());
        assertEquals(sessionId, result.sessionId());
    }

    // --- getSessionHistory Tests ---
    @Test
    void getSessionHistory_ShouldReturnPagedDto() {
        Pageable pageable = PageRequest.of(0, 10);
        ChatMessage message = ChatMessage.builder().id(UUID.randomUUID()).session(mockSession).build();
        Page<ChatMessage> page = new PageImpl<>(Collections.singletonList(message));

        when(messageRepository.findBySessionId(sessionId, pageable)).thenReturn(page);

        Page<ChatHistoryResponse> result = chatService.getSessionHistory(sessionId, pageable);

        assertEquals(1, result.getTotalElements());
        verify(messageRepository).findBySessionId(sessionId, pageable);
    }

    // --- findSessionOrThrow ---
    @Test
    void findSessionOrThrow_ShouldThrow_WhenNotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> chatService.addMessage(sessionId, null));

        assertTrue(ex.getMessage().contains("Session not found"));
    }

}
