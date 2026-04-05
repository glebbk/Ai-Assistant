package com.example.ai.assistant.controllers;

import com.example.ai.assistant.models.Article;
import com.example.ai.assistant.services.ArticleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    public Article create(@RequestBody Article article) {

        return articleService.create(article);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Article> getAll() {
        return articleService.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @articleSecurity.isOwner(#id)")
    public Article update(@PathVariable Long id, @RequestBody Article article) {
        return articleService.update(id, article);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        articleService.delete(id);
    }
}
