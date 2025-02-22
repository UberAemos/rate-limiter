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

import java.io.IOException;
import java.util.List;

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
    void getRules() throws IOException {
        Cache cache = cacheManager.getCache(RedisConfig.RATE_LIMITER_RULES);
        assertNull(cache.get("getRules"));
        List<RateLimiterRule> rules = service.getRules();

        assertNotNull(rules);
        assertEquals(1, rules.size());
        RateLimiterRule rateLimiterRule = rules.get(0);
        assertEquals("api/test", rateLimiterRule.endpoint());
        assertEquals("GET", rateLimiterRule.method());
        assertEquals(10, rateLimiterRule.window());
        assertEquals(5, rateLimiterRule.limit());
        assertNotNull(cache.get("getRules"));
    }
}