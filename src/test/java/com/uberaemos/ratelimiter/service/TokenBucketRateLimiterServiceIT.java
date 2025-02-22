package com.uberaemos.ratelimiter.service;

import com.redis.testcontainers.RedisContainer;
import com.uberaemos.ratelimiter.model.RateLimiterRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class TokenBucketRateLimiterServiceIT {

    @Autowired
    private TokenBucketRateLimiterService rateLimiterService;

    @MockitoBean
    private RuleLoaderService ruleLoaderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Container
    @ServiceConnection(name = "redis")
    public static final GenericContainer<?> redis = new RedisContainer("redis:latest")
            .withExposedPorts(6379);

    @AfterAll
    static void afterAll() {
        redis.stop();
    }

    @Test
    void testRateLimiterWithConcurrentRequests() throws InterruptedException {
        String client = "client";
        redisTemplate.opsForValue().set("rate_limit:".concat(client), "5," + Instant.now().getEpochSecond());
        String endpoint = "test";
        when(ruleLoaderService.getRule(endpoint)).thenReturn(new RateLimiterRule(endpoint, 5, 0));
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger allowedRequests = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    if (rateLimiterService.checkRateLimit("client", endpoint)) {
                        allowedRequests.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Expect only up to 5 requests to succeed based on capacity
        assertEquals(5, allowedRequests.get(), "Expected only 5 requests to pass the rate limit");
    }
}