package com.uberaemos.ratelimiter.model;

import java.io.Serializable;

public record RateLimiterRule(String endpoint, String method, int limit, int window) implements Serializable {
}
