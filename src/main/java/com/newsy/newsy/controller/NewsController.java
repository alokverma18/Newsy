package com.newsy.newsy.controller;

import com.newsy.newsy.dto.NewsDTO;
import com.newsy.newsy.model.News;
import com.newsy.newsy.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = {"${FRONTEND_URL:http://localhost:4200}"})
@Slf4j
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * GET /api/news/{category}
     * Returns the latest 5 articles for a specific category
     */
    @GetMapping("/{category}")
    public ResponseEntity<?> getNewsByCategory(@PathVariable String category) {
        try {
            log.info("REST API: Getting news for category: {}", category);

            // Capitalize first letter for consistency
            String formattedCategory = capitalizeFirstLetter(category);

            List<News> newsList = newsService.getNewsByCategory(formattedCategory);
            List<NewsDTO> newsDTO = newsList.stream()
                    .map(NewsDTO::fromEntity)
                    .toList();

            if (newsDTO.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "message", "No news found for category: " + formattedCategory,
                    "category", formattedCategory,
                    "articles", Collections.emptyList()
                ));
            }

            return ResponseEntity.ok(Map.of(
                "category", formattedCategory,
                "count", newsDTO.size(),
                "articles", newsDTO
            ));

        } catch (Exception e) {
            log.error("Error fetching news for category {}: {}", category, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch news for category: " + category));
        }
    }

    /**
     * GET /api/news
     * Returns all latest articles grouped by category
     */
    @GetMapping
    public ResponseEntity<?> getAllNews() {
        try {
            log.info("REST API: Getting all news");

            List<News> allNews = newsService.getAllNews();

            // Group news by category
            Map<String, List<NewsDTO>> groupedNews = allNews.stream()
                    .collect(Collectors.groupingBy(
                        News::getCategory,
                        Collectors.mapping(NewsDTO::fromEntity, Collectors.toList())
                    ));

            // Ensure each category has max 4 articles
            Map<String, List<NewsDTO>> limitedGroupedNews = new LinkedHashMap<>();
            groupedNews.forEach((category, articles) -> limitedGroupedNews.put(category, articles.stream()
                    .limit(4)
                    .collect(Collectors.toList())));

            return ResponseEntity.ok(Map.of(
                "totalCategories", limitedGroupedNews.size(),
                "totalArticles", limitedGroupedNews.values().stream()
                        .mapToInt(List::size).sum(),
                "news", limitedGroupedNews
            ));

        } catch (Exception e) {
            log.error("Error fetching all news: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch news"));
        }
    }

    /**
     * POST /api/news/fetch
     * Manual trigger to fetch news (for testing/admin purposes)
     */
    @PostMapping("/fetch")
    public ResponseEntity<?> manualFetchNews() {
        try {
            log.info("REST API: Manual news fetch triggered");
            newsService.fetchAndStoreNews();
            return ResponseEntity.ok(Map.of(
                "message", "News fetch completed successfully",
                "timestamp", new Date()
            ));
        } catch (Exception e) {
            log.error("Error in manual news fetch: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch news: " + e.getMessage()));
        }
    }

    /**
     * Helper method to capitalize first letter of a string
     */
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
