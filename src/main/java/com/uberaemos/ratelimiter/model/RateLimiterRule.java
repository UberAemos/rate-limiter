package com.uberaemos.ratelimiter.model;

import java.io.Serializable;

public record RateLimiterRule(String endpoint, int capacity, int refillRate) implements Serializable {
}
