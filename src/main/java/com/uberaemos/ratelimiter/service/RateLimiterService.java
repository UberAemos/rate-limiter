package com.uberaemos.ratelimiter.service;

public interface RateLimiterService {
    boolean checkRateLimit(String clientId, String endpoint);
}
