package com.capgemini.chat_history.controller;

import com.capgemini.chat_history.dto.ChatHistoryResponse;
import com.capgemini.chat_history.dto.ChatSessionResponse;
import com.capgemini.chat_history.dto.MessageRequest;
import com.capgemini.chat_history.dto.SessionRequest;
import com.capgemini.chat_history.service.ChatStorageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Chat Storage Microservice", description = "APIs for managing RAG chat sessions and history")
public class ChatStorageController {
    private final ChatStorageService chatService;

    public ChatStorageController(ChatStorageService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "Start a new session", description = "Requires userId and title in the body.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Session created"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Invalid API Key"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<ChatSessionResponse> create(@Valid @RequestBody SessionRequest request) {
        return new ResponseEntity<>(chatService.createSession(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update session", description = "Rename title or toggle favorite status for a Chat session.")
    public ResponseEntity<ChatSessionResponse> update(@PathVariable UUID id, @RequestBody SessionRequest request) {
        return ResponseEntity.ok(chatService.updateSessionMetadata(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete session", description = "Deletes session and all its messages (Cascade).")
    @ApiResponse(responseCode = "204", description = "Chat Session deleted")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        chatService.deleteSession(id);
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Add message", description = "Add a USER or ASSISTANT's message to the session.")
    public ResponseEntity<ChatHistoryResponse> addMessage(@PathVariable UUID id, @Valid @RequestBody MessageRequest request) {
        return new ResponseEntity<>(chatService.addMessage(id, request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get history", description = "Retrieve paginated messages for a specific session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chat History retrieved"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<Page<ChatHistoryResponse>> getHistory(@PathVariable UUID id, @PageableDefault Pageable page) {
        return ResponseEntity.ok(chatService.getSessionHistory(id, page));
    }
}
