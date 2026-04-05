package com.example.ai.assistant.repositories;

import com.example.ai.assistant.models.Chat;
import com.example.ai.assistant.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserOrderByUpdatedAtDesc(User user);
    Optional<Chat> findByIdAndUser(Long id, User user);
}