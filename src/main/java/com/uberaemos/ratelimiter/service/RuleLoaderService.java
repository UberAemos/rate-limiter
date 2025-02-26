package com.uberaemos.ratelimiter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberaemos.ratelimiter.config.RedisConfig;
import com.uberaemos.ratelimiter.exception.BusinessError;
import com.uberaemos.ratelimiter.exception.BusinessException;
import com.uberaemos.ratelimiter.model.RateLimiterRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.List;

@Service
public class RuleLoaderService {
    private final ObjectMapper objectMapper;
    private final String rulesFile;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLoaderService.class);

    public RuleLoaderService(ObjectMapper objectMapper,
                             @Value("${rate-limiter.rules.destination}") String rulesFile) {
        this.objectMapper = objectMapper;
        this.rulesFile = rulesFile;
    }

    @Cacheable(value = RedisConfig.RATE_LIMITER_RULES, key = "#endpoint")
    public RateLimiterRule getRule(String endpoint) {
        LOGGER.info("Finding rule for endpoint: {}", endpoint);

        try {
            LOGGER.debug("Attempting to read rate limiter rules from file: {}", rulesFile);
            List<RateLimiterRule> rateLimiterRules = objectMapper.readValue(ResourceUtils.getFile(rulesFile), new TypeReference<>() {
            });

            LOGGER.info("Successfully loaded rate limiter rules from file: {}", rulesFile);
            LOGGER.debug("Total rate limiter rules loaded: {}", rateLimiterRules.size());

            return rateLimiterRules.stream()
                    .filter(rule -> endpoint.equals(rule.endpoint()))
                    .findFirst()
                    .orElseThrow(() -> {
                        LOGGER.warn("No matching rule found for endpoint: {}. Available endpoints: {}",
                                endpoint, rateLimiterRules.stream().map(RateLimiterRule::endpoint).toList());
                        return new BusinessException(BusinessError.RULE_NOT_FOUND);
                    });
        } catch (IOException e) {
            LOGGER.error("Failed to read rules file: {} due to {}", rulesFile, e.getMessage(), e);
            LOGGER.warn("Returning default rate limiter rule for endpoint: {} - (Limit: 100, Burst: 2)", endpoint);
            return new RateLimiterRule(endpoint, 100, 2);
        }
    }

}
