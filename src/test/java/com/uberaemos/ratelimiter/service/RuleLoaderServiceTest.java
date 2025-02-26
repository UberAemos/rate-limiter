package com.uberaemos.ratelimiter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberaemos.ratelimiter.exception.BusinessError;
import com.uberaemos.ratelimiter.exception.BusinessException;
import com.uberaemos.ratelimiter.model.RateLimiterRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RuleLoaderServiceTest {

    @Test
    void testGetRule_RuleNotFound() {
        RuleLoaderService ruleLoaderService = new RuleLoaderService(new ObjectMapper(), "classpath:rate-limiter-rules.json");
        BusinessException exception = assertThrows(BusinessException.class,
                () -> ruleLoaderService.getRule("/api/missing"));
        assertEquals(BusinessError.RULE_NOT_FOUND, exception.getError());
    }

    @Test
    void testGetRule_FileReadException() {
        RuleLoaderService ruleLoaderService = new RuleLoaderService(new ObjectMapper(), "classpath:invalid-rate-limiter-rules.json");

        RateLimiterRule rule = ruleLoaderService.getRule("/api/fallback");

        assertNotNull(rule);
        assertEquals("/api/fallback", rule.endpoint());
        assertEquals(2, rule.refillRate());
        assertEquals(100, rule.capacity());
    }

}