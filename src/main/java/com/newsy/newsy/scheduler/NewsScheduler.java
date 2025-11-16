package com.newsy.newsy.scheduler;

import com.newsy.newsy.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NewsScheduler {

    private final NewsService newsService;

    public NewsScheduler(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Scheduled job to fetch news daily at 8:00 AM
     * Cron expression: 0 0 8 * * ? (sec min hour day month weekday)
     */
    @Scheduled(cron = "${news.fetch.cron}", zone = "${app.timezone:UTC}")
    public void fetchDailyNews() {
        log.info("=== Starting Daily News Fetch Job ===");
        try {
            newsService.fetchAndStoreNews();
            log.info("=== Daily News Fetch Job Completed Successfully ===");
        } catch (Exception e) {
            log.error("=== Daily News Fetch Job Failed: {} ===", e.getMessage(), e);
        }
    }

    /**
     * Optional: Run on application startup (for testing)
     */
//    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void fetchNewsOnStartup() {
        log.info("=== Fetching news on application startup ===");
        try {
            newsService.fetchAndStoreNews();
        } catch (Exception e) {
            log.error("=== Startup news fetch failed: {} ===", e.getMessage(), e);
        }
    }
}

