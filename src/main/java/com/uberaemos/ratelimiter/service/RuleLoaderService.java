package com.uberaemos.ratelimiter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberaemos.ratelimiter.config.RedisConfig;
import com.uberaemos.ratelimiter.model.RateLimiterRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.List;

@Service
public class RuleLoaderService {
    private final ObjectMapper objectMapper;
    private static final String RULES_FILE = "classpath:rate-limiter-rules.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLoaderService.class);

    public RuleLoaderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = RedisConfig.RATE_LIMITER_RULES, key = "#root.methodName")
    public List<RateLimiterRule> getRules() throws IOException {
        LOGGER.info("Returning rules");
        return objectMapper.readValue(ResourceUtils.getFile(RULES_FILE), new TypeReference<>() {
        });
    }

}
