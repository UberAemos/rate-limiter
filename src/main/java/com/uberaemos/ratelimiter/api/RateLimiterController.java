package com.uberaemos.ratelimiter.api;

import com.uberaemos.ratelimiter.service.RateLimiterService;
import com.uberaemos.ratelimiter.service.TokenBucketRateLimiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    public RateLimiterController(TokenBucketRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/rate-limiter")
    public ResponseEntity<?> checkRateLimit(@RequestHeader(value = "X-Client-ID") String clientId,
                                            @RequestHeader(value = "X-Original-Path") String endpoint) {
        if (rateLimiterService.checkRateLimit(clientId, endpoint)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(429).build();
        }
    }
}
