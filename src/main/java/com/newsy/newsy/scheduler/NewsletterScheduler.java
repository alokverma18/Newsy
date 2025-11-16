package com.newsy.newsy.scheduler;

import com.newsy.newsy.model.News;
import com.newsy.newsy.model.Subscriber;
import com.newsy.newsy.service.EmailService;
import com.newsy.newsy.service.NewsService;
import com.newsy.newsy.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsletterScheduler {

    private final SubscriptionService subscriptionService;
    private final EmailService emailService;
    private final NewsService newsService;

    @Value("${app.max-articles-per-mail:8}")
    private int maxArticlesPerMail;

//    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)

    @Scheduled(cron = "${newsletter.email.cron}", zone = "${app.timezone:UTC}")
    public void sendDaily() {
        log.info("Executing Daily Newsletter Job");
        List<Subscriber> subs = subscriptionService.getVerifiedActiveSubscribers();
        log.info("Found {} verified active subscribers", subs == null ? 0 : subs.size());

        for (Subscriber s : subs) {
            String email = s == null ? "<null-subscriber>" : s.getEmail();
            log.info("Compiling Newsletter for: {}", email);
            try {
                List<String> categories = s.getCategories() == null ? Collections.emptyList() : s.getCategories();
                log.info("Subscriber {} categories: {}", email, categories);

                List<Map<String, String>> articles = categories.stream()
                        .filter(cat -> cat != null && !cat.isBlank())
                        .map(cat -> cat.trim().toLowerCase(Locale.ROOT))
                        .flatMap(cat -> {
                            log.debug("Fetching top articles for category: {}", cat);
                            List<News> fetched = newsService.fetchTopArticles(cat, 2);
                            int fetchedCount = fetched == null ? 0 : fetched.size();
                            log.info("Fetched {} articles for category: {}", fetchedCount, cat);
                            return (fetched == null ? Collections.<News>emptyList() : fetched).stream();
                        })
                        .limit(maxArticlesPerMail)
                        .map(a -> Map.of(
                                "title", a.getTitle(),
                                "url", a.getUrl(),
                                "summary", a.getDescription()
                        ))
                        .collect(Collectors.toList());
                log.info("Total articles compiled for {}: {}", email, articles.size());
                if (articles.isEmpty()) {
                    log.info("No articles found for {}, skipping email send", email);
                    continue;
                }

                log.info("Sending newsletter to {} with {} articles", email, articles.size());
                emailService.sendNewsletter(email, "Your Newsy Daily", articles, s.getVerificationToken());
                log.info("Newsletter successfully sent to {}", email);

            } catch (MessagingException mex) {
                log.error("Failed to send newsletter to {}: {}", email, mex.getMessage(), mex);
            } catch (Exception ex) {
                log.error("Unexpected error compiling/sending newsletter for {}: {}", email, ex.getMessage(), ex);
            }
        }
    }
}

