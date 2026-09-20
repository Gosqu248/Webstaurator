package com.gosqu.notificationservice.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${notification.mail.from}")
    private String fromAddress;

    @Value("${notification.rate-limit.max-per-window}")
    private int maxPerWindow;

    @Value("${notification.rate-limit.window-seconds}")
    private long windowSeconds;

    public void send(String toEmail, String subject, String body) {
        if (isRateLimited(toEmail)) {
            log.warn("action=email_rate_limited to={} subject={}", toEmail, subject);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.debug("action=email_sent to={} subject={}", toEmail, subject);
    }

    // Fixed-window counter (md/todo-v2/02-redis-mid-senior.md, sekcja 4): INCR jest atomowy, wiec
    // rownolegle konsumowane rekordy Kafki dla tego samego adresata nigdy nie zgubia inkrementacji.
    // EXPIRE ustawiamy tylko przy pierwszym trafieniu w oknie (count == 1), zeby kolejne wiadomosci
    // nie resetowaly okna w nieskonczonosc.
    private boolean isRateLimited(String toEmail) {
        String key = "ratelimit:notification:" + toEmail;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        return count != null && count > maxPerWindow;
    }

}

