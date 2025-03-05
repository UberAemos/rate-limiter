package com.uberaemos.ratelimiter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

public class RateLimiterMonitor {

    private final Timer timer;
    private final Counter allowedRequestsCounter;
    private final Counter blockedRequestsCounter;

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterMonitor.class);

    public RateLimiterMonitor(MeterRegistry meterRegistry) {
        this.timer = Timer.builder("rate_limit_check_latency_seconds")
                .serviceLevelObjectives(
                        Duration.ofMillis(10),
                        Duration.ofMillis(25),
                        Duration.ofMillis(50),
                        Duration.ofMillis(100)
                )
                .maximumExpectedValue(Duration.ofMillis(500))
                .register(meterRegistry);

        this.allowedRequestsCounter = Counter.builder("rate_limit.allowed")
                .description("Number of requests allowed by rate limiter")
                .register(meterRegistry);

        this.blockedRequestsCounter = Counter.builder("rate_limit.blocked")
                .description("Number of requests blocked by rate limiter")
                .register(meterRegistry);
    }


    public boolean monitorRateLimiter(Supplier<Boolean> rateLimiterResult) {
        long startTime = System.nanoTime();

        boolean allowed = false;

        try {
            allowed = rateLimiterResult.get();
            return allowed;
        } finally {
            long durationNanos = System.nanoTime() - startTime;
            timer.record(Duration.ofNanos(durationNanos));

            if (allowed) {
                allowedRequestsCounter.increment();
            } else {
                blockedRequestsCounter.increment();
            }

            LOGGER.debug("Rate limiter decision in: {}", durationNanos / 1_000_000);
        }
    }
}
