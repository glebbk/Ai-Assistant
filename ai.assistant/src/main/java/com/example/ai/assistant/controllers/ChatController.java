package com.example.ai.assistant.controllers;

import com.example.ai.assistant.dto.ChatRequest;
import com.example.ai.assistant.dto.ChatResponse;
import com.example.ai.assistant.models.Chat;
import com.example.ai.assistant.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@PreAuthorize("isAuthenticated()")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        Chat chat = chatService.sendMessage(request.getMessage(), request.getChatId());
        return ResponseEntity.ok(convertToDto(chat));
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatResponse>> getUserChats() {
        List<Chat> chats = chatService.getUserChats();
        List<ChatResponse> response = chats.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chats/{chatId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable Long chatId) {
        Chat chat = chatService.getChat(chatId);
        return ResponseEntity.ok(convertToDto(chat));
    }

    @DeleteMapping("/chats/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }

    private ChatResponse convertToDto(Chat chat) {
        ChatResponse dto = new ChatResponse();
        dto.setId(chat.getId());
        dto.setTitle(chat.getTitle());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setUpdatedAt(chat.getUpdatedAt());

        List<ChatResponse.MessageDto> messageDtos = chat.getMessages().stream()
                .map(msg -> new ChatResponse.MessageDto(
                        msg.getId(),
                        msg.getContent(),
                        msg.getRole(),
                        msg.getCreatedAt()
                ))
                .collect(Collectors.toList());
        dto.setMessages(messageDtos);

        return dto;
    }
}