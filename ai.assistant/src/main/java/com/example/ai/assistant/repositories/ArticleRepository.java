package com.example.ai.assistant.repositories;

import com.example.ai.assistant.models.Article;
import com.example.ai.assistant.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByAuthor(User author);

    List<Article> findByTitleContaining(String keyword);
}
