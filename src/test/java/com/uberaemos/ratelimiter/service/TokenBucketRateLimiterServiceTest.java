package com.uberaemos.ratelimiter.service;

import com.uberaemos.ratelimiter.exception.BusinessError;
import com.uberaemos.ratelimiter.exception.BusinessException;
import com.uberaemos.ratelimiter.model.RateLimiterRule;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBucketRateLimiterServiceTest {

    @Mock
    private RuleLoaderService ruleLoaderService;

    @Mock
    private RedisOperations<String, String> redisOperations;

    @Mock
    private RedisScript<Long> script;

    @Spy
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();

    @InjectMocks
    private TokenBucketRateLimiterService tokenBucketRateLimiterService;

    @Test
    void shouldThrowUnknownError_whenRedisFails() {
        when(ruleLoaderService.getRule("endPoint")).thenReturn(new RateLimiterRule("endpoint", 100, 2));
        when(redisOperations.execute(
                any(RedisScript.class),
                eq(List.of("rate_limit:clientId")),
                eq("100"), eq("2"),
                anyString(),
                eq("1")))
                .thenThrow(RuntimeException.class);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> tokenBucketRateLimiterService.checkRateLimit("clientId", "endPoint"));
        assertEquals(BusinessError.UNKNOWN_ERROR, businessException.getError());
    }

}