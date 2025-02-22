package com.uberaemos.ratelimiter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;

@EnableCaching
@Configuration
public class RedisConfig {

    public static final String RATE_LIMITER_RULES = "rateLimiterRules";

    @Value("${rate-limiter.cache.time-to-live.rate-limiter-rules}")
    private int rateLimiterRulesTtl;

    @Bean
    public RedisCacheManagerBuilderCustomizer cacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(RATE_LIMITER_RULES, RedisCacheConfiguration
                        .defaultCacheConfig().entryTtl(Duration.ofSeconds(rateLimiterRulesTtl)));
    }

    @Bean
    public RedisScript<Long> script() {
        return RedisScript.of(new ClassPathResource("bucket.lua"), Long.class);
    }
}
