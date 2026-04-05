package com.example.ai.assistant.services;

import com.example.ai.assistant.models.Article;
import com.example.ai.assistant.repositories.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article create(Article article) {
        article.setCreatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Transactional
    public Article update(Long id, Article articleDetails) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id " + id));

        existingArticle.setTitle(articleDetails.getTitle());
        existingArticle.setContent(articleDetails.getContent());
        existingArticle.setUpdatedAt(LocalDateTime.now());

        return articleRepository.save(existingArticle);
    }

    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
