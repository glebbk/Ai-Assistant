package com.example.ai.assistant.dto;

public class ChatRequest {
    private String message;
    private Long chatId; // optional, if null create new chat

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}