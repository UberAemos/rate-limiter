package com.uberaemos.ratelimiter.metric;

import io.prometheus.metrics.core.datapoints.Timer;
import io.prometheus.metrics.core.metrics.Histogram;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LatencyTrackingAspect {

    @Around("@annotation(trackLatency)")
    public Object trackMethodLatency(ProceedingJoinPoint joinPoint, TrackLatency trackLatency) throws Throwable {
        String metricName = trackLatency.metricName();

        Histogram histogram = createHistogram(metricName);

        Timer timer = histogram.startTimer();
        try {
            return joinPoint.proceed();
        } finally {
            timer.observeDuration();
            timer.close();
        }
    }

    private Histogram createHistogram(String metricName) {
        return Histogram.builder()
                .name(metricName)
                .help("Latency for method: " + metricName)
                .register();
    }
}
