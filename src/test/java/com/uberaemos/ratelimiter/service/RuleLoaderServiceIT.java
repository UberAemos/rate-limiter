package com.uberaemos.ratelimiter.service;

import com.redis.testcontainers.RedisContainer;
import com.uberaemos.ratelimiter.config.RedisConfig;
import com.uberaemos.ratelimiter.model.RateLimiterRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class RuleLoaderServiceIT {

    @Autowired
    private RuleLoaderService service;

    @Autowired
    private CacheManager cacheManager;

    @Container
    @ServiceConnection(name = "redis")
    public static final GenericContainer<?> redis = new RedisContainer("redis:latest")
            .withExposedPorts(6379);

    @AfterAll
    static void afterAll() {
        redis.stop();
    }

    @Test
    void getRules() {
        Cache cache = cacheManager.getCache(RedisConfig.RATE_LIMITER_RULES);
        assertNull(cache.get("/login"));
        RateLimiterRule rule = service.getRule("/login");

        assertNotNull(rule);
        assertEquals("/login", rule.endpoint());
        assertEquals(5, rule.capacity());
        assertEquals(1, rule.refillRate());
        assertNotNull(cache.get("/login"));
    }
}