package ru.khalitovaae.restaurantvoting.config.cache;


import org.ehcache.expiry.ExpiryPolicy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static java.time.LocalDate.now;

public class EvictAtMidnightCache implements ExpiryPolicy {

    @Override
    public Duration getExpiryForCreation(Object key, Object value) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resetAt = LocalDateTime.of(now(), LocalTime.MAX);
        long diff = ChronoUnit.SECONDS.between(now, resetAt);
        return Duration.of(diff, ChronoUnit.SECONDS);
    }

    @Override
    public Duration getExpiryForAccess(Object key, Supplier value) {
        return null;
    }

    @Override
    public Duration getExpiryForUpdate(Object key, Supplier oldValue, Object newValue) {
        return null;
    }
}
