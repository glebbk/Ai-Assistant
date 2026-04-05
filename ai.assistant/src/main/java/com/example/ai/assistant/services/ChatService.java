package com.example.ai.assistant.services;

import com.example.ai.assistant.models.Chat;
import com.example.ai.assistant.models.Message;
import com.example.ai.assistant.models.User;
import com.example.ai.assistant.repositories.ChatRepository;
import com.example.ai.assistant.repositories.MessageRepository;
import com.example.ai.assistant.repositories.UserRepository;
import org.springframework.ai.chat.ChatClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatService(ChatClient chatClient,
                       ChatRepository chatRepository,
                       MessageRepository messageRepository,
                       UserRepository userRepository) {
        this.chatClient = chatClient;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public Chat sendMessage(String messageText, Long chatId) {
        User currentUser = getCurrentUser();
        Chat chat;

        if (chatId == null) {
            String title = messageText.length() > 30
                    ? messageText.substring(0, 27) + "..."
                    : messageText;
            chat = new Chat(title, currentUser);
            chat = chatRepository.save(chat);
        } else {
            chat = chatRepository.findByIdAndUser(chatId, currentUser)
                    .orElseThrow(() -> new RuntimeException("Chat not found"));
        }

        Message userMessage = new Message(messageText, "user", chat);
        messageRepository.save(userMessage);
        chat.addMessage(userMessage);

        String aiResponse = chatClient.call(messageText);

        Message aiMessage = new Message(aiResponse, "assistant", chat);
        messageRepository.save(aiMessage);
        chat.addMessage(aiMessage);

        return chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public List<Chat> getUserChats() {
        User currentUser = getCurrentUser();
        return chatRepository.findByUserOrderByUpdatedAtDesc(currentUser);
    }

    @Transactional(readOnly = true)
    public Chat getChat(Long chatId) {
        User currentUser = getCurrentUser();
        return chatRepository.findByIdAndUser(chatId, currentUser)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Transactional
    public void deleteChat(Long chatId) {
        User currentUser = getCurrentUser();
        Chat chat = chatRepository.findByIdAndUser(chatId, currentUser)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        chatRepository.delete(chat);
    }
}