package com.newsy.newsy.service;

import com.newsy.newsy.dto.NewsApiResponse;
import com.newsy.newsy.model.News;
import com.newsy.newsy.repository.NewsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;

    @Value("${newsdata.api.key}")
    private String apiKey;

    @Value("${newsdata.api.url}")
    private String apiUrl;

    @Value("${newsdata.maxArticleAgeDays:2}")
    private int maxArticleAgeDays;

    private static final String[] CATEGORIES = {"technology", "sports", "business", "education", "entertainment"};
    private static final int ARTICLES_PER_CATEGORY = 4;
    private static final int API_FETCH_SIZE = 10; // Fetch more to filter client-side for date and duplicates

    public NewsService(NewsRepository newsRepository, RestTemplate restTemplate) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch news from NewsData.io for all categories
     */
    public void fetchAndStoreNews() {
        log.info("Starting scheduled news fetch at {}", LocalDateTime.now());

        for (String category : CATEGORIES) {
            try {
                fetchNewsForCategory(category);
            } catch (Exception e) {
                log.error("Error fetching news for category {}: {}", category, e.getMessage(), e);
            }
        }

        log.info("Completed scheduled news fetch");
    }

    /**
     * Fetch news for a specific category from NewsData.io
     */
    private void fetchNewsForCategory(String category) {
        log.info("Fetching latest news for category: {}", category);

        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("apikey", apiKey)
                    .queryParam("category", category.toLowerCase())
                    .queryParam("language", "en")
                    .queryParam("size", API_FETCH_SIZE)
                    .queryParam("removeduplicate", "1")
                    .toUriString();

            log.debug("API URL: {}", url.replace(apiKey, "***"));
            log.info("Fetching latest {} articles for category: {} (will filter to {} recent articles within {} days)",
                    API_FETCH_SIZE, category, ARTICLES_PER_CATEGORY, maxArticleAgeDays);

            NewsApiResponse response = restTemplate.getForObject(url, NewsApiResponse.class);

            if (response != null && "success".equals(response.getStatus())
                    && response.getResults() != null && !response.getResults().isEmpty()) {

                List<News> newsList = response.getResults().stream()
                        .map(article -> mapToNewsEntity(article, capitalizeFirstLetter(category)))
                        .filter(this::isArticleRecent)
                        .limit(ARTICLES_PER_CATEGORY)
                        .collect(Collectors.toList());

                if (!newsList.isEmpty()) {
                    newsRepository.deleteByCategory(capitalizeFirstLetter(category));
                    newsRepository.saveAll(newsList);

                    log.info("Successfully saved {} recent articles for category: {} (filtered from {} fetched)",
                            newsList.size(), capitalizeFirstLetter(category), response.getResults().size());
                } else {
                    log.warn("No recent articles (within {} days) found for category: {} after filtering {} results",
                            maxArticleAgeDays, category, response.getResults().size());
                }
            } else {
                log.warn("No articles found for category: {}. Status: {}", category,
                        response != null ? response.getStatus() : "null response");
            }

        } catch (Exception e) {
            log.error("Failed to fetch news for category {}: {}", category, e.getMessage(), e);
            throw new RuntimeException("Error fetching news for category: " + category, e);
        }
    }

    /**
     * Check if article was published within the configured time period
     */
    private boolean isArticleRecent(News news) {
        if (news.getPublishedAt() == null) {
            log.warn("Article has no publication date: {}", news.getTitle());
            return false;
        }

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(maxArticleAgeDays);
        boolean isRecent = news.getPublishedAt().isAfter(cutoffDate);

        if (!isRecent) {
            log.debug("Filtering out old article '{}' published at {} (older than {} days)",
                    news.getTitle(), news.getPublishedAt(), maxArticleAgeDays);
        }

        return isRecent;
    }

    /**
     * Map NewsData.io article to News entity
     */
    private News mapToNewsEntity(NewsApiResponse.Result article, String category) {
        String title = article.getTitle() != null ? article.getTitle() : "No Title";
        String author = extractAuthor(article);
        String source = article.getSourceName() != null ? article.getSourceName() :
                (article.getSourceId() != null ? article.getSourceId() : "Unknown Source");
        String url = article.getLink() != null ? article.getLink() : "";
        LocalDateTime publishedAt = parseDateTime(article.getPubDate());
        String description = article.getDescription() != null ? article.getDescription() :
                (article.getContent() != null ?
                        (article.getContent().length() > 500 ? article.getContent().substring(0, 500) + "..." : article.getContent())
                        : "No description available");
        String imageUrl = article.getImageUrl() != null ? article.getImageUrl() : getDefaultImageForCategory(category);
        String sourceIcon = article.getSourceIcon() != null && !article.getSourceIcon().isEmpty()
                ? article.getSourceIcon()
                : generateFallbackSourceIcon(article.getSourceId());

        return new News(title, author, source, url, publishedAt, category, description, imageUrl, sourceIcon);
    }

    /**
     * Get default placeholder image for category
     */
    private String getDefaultImageForCategory(String category) {
        return switch (category.toLowerCase()) {
            case "technology" -> "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800";
            case "sports" -> "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=800";
            case "business" -> "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=800";
            case "education" -> "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0?w=800";
            case "entertainment" -> "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800";
            default -> "https://images.unsplash.com/photo-1504711434969-e33886168f5c?w=800";
        };
    }

    /**
     * Generate fallback source icon URL using Google's favicon service
     * Only used if NewsData.io doesn't provide source_icon
     */
    private String generateFallbackSourceIcon(String sourceId) {
        if (sourceId == null || sourceId.isEmpty()) {
            return "https://www.google.com/s2/favicons?domain=news.com&sz=64";
        }
        // Use the source ID to generate a favicon URL
        return "https://www.google.com/s2/favicons?domain=" + sourceId + "&sz=64";
    }

    /**
     * Extract author name from the article
     */
    private String extractAuthor(NewsApiResponse.Result article) {
        if (article.getCreator() != null && !article.getCreator().isEmpty()) {
            return article.getCreator().stream()
                    .filter(name -> name != null && !name.isEmpty())
                    .findFirst()
                    .orElse("Unknown Author");
        }
        return "Unknown Author";
    }

    /**
     * Parse date-time string from NewsData.io API
     * Format: "2025-11-02 14:30:00" or ISO format
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }

        try {
            // Try ISO format first (e.g., "2025-11-02T14:30:00")
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                // Try NewsData.io format: "2025-11-02 14:30:00"
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException ex) {
                try {
                    // Try with 'T' separator
                    return LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
                } catch (Exception exc) {
                    log.warn("Could not parse date: {}, using current time", dateTimeStr);
                    return LocalDateTime.now();
                }
            }
        }
    }

    /**
     * Get latest articles for a specific category
     * Sorted by fetchedAt (most recently fetched articles first), then by publishedAt
     * This ensures users always see the freshest content from our latest fetch
     */
    public List<News> getNewsByCategory(String category) {
        log.info("Fetching news for category: {}", category);
        // Use fetchedAt sorting to ensure we show articles from the latest fetch
        List<News> articles = newsRepository.findTop5ByCategoryIgnoreCaseOrderByFetchedAtDescPublishedAtDesc(category);

        // Limit to 5 articles (in case the @Query doesn't limit properly)
        return articles.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * Get all latest articles
     * Sorted by fetchedAt (most recently fetched articles first), then by publishedAt
     * This ensures users see articles from our most recent fetch across all categories
     */
    public List<News> getAllNews() {
        log.info("Fetching all news");
        // Use fetchedAt sorting to ensure we show articles from the latest fetch
        return newsRepository.findAllByOrderByFetchedAtDescPublishedAtDesc();
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

    public List<News> fetchTopArticles(String cat, int i) {
        return newsRepository.findTop5ByCategoryIgnoreCase(cat)
                .stream()
                .limit(i)
                .collect(Collectors.toList());
    }
}
