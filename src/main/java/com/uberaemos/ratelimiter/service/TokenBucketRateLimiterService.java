package com.uberaemos.ratelimiter.service;

import com.uberaemos.ratelimiter.exception.BusinessError;
import com.uberaemos.ratelimiter.exception.BusinessException;
import com.uberaemos.ratelimiter.model.RateLimiterRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
public class TokenBucketRateLimiterService implements RateLimiterService {

    private final RuleLoaderService ruleLoaderService;
    private final RedisOperations<String, String> redisOperations;
    private final RedisScript<Long> script;

    public TokenBucketRateLimiterService(RuleLoaderService ruleLoaderService,
                                         RedisOperations<String, String> redisOperations,
                                         RedisScript<Long> script) {
        this.ruleLoaderService = ruleLoaderService;
        this.redisOperations = redisOperations;
        this.script = script;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenBucketRateLimiterService.class);

    @Override
    public boolean checkRateLimit(String clientId, String endpoint) {
        LOGGER.info("Checking rate limit for client: {}, endpoint: {}", clientId, endpoint);

        RateLimiterRule rule = ruleLoaderService.getRule(endpoint);
        LOGGER.info("Loaded rate limit rule: capacity={}, refillRate={}", rule.capacity(), rule.refillRate());

        String key = "rate_limit:".concat(clientId);
        int refillRate = rule.refillRate();
        int capacity = rule.capacity();

        long now = Instant.now().getEpochSecond();
        try {
            long result = redisOperations.execute(
                    script,
                    Collections.singletonList(key),
                    String.valueOf(capacity),
                    String.valueOf(refillRate),
                    String.valueOf(now),
                    "1"
            );

            LOGGER.info("Rate limit check result for client {}: {}", clientId, (result == 1 ? "Allowed" : "Blocked"));
            return result == 1;
        } catch (Exception e) {
            LOGGER.error("Rate limit check failed for client {}: {}", clientId, e.getMessage(), e);
            throw new BusinessException(BusinessError.UNKNOWN_ERROR);
        }
    }
}
