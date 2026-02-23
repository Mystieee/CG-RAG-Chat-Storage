package com.capgemini.chat_history.controller;

import com.capgemini.chat_history.dto.ChatHistoryResponse;
import com.capgemini.chat_history.dto.ChatSessionResponse;
import com.capgemini.chat_history.dto.MessageRequest;
import com.capgemini.chat_history.dto.SessionRequest;
import com.capgemini.chat_history.service.ChatStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "Chat Management", description = "APIs for managing RAG chat sessions and history")
public class ChatStorageController {
    private final ChatStorageService chatService;

    public ChatStorageController(ChatStorageService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatSessionResponse> create(@Valid @RequestBody SessionRequest request) {
        return new ResponseEntity<>(chatService.createSession(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChatSessionResponse> update(@PathVariable UUID id, @RequestBody SessionRequest request) {
        return ResponseEntity.ok(chatService.updateSessionMetadata(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        chatService.deleteSession(id);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ChatHistoryResponse> addMessage(@PathVariable UUID id, @Valid @RequestBody MessageRequest request) {
        return new ResponseEntity<>(chatService.addMessage(id, request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<Page<ChatHistoryResponse>> getHistory(@PathVariable UUID id, @PageableDefault Pageable page) {
        return ResponseEntity.ok(chatService.getSessionHistory(id, page));
    }


}
