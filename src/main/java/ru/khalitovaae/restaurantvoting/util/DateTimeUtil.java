package ru.khalitovaae.restaurantvoting.util;

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;

@UtilityClass
public class DateTimeUtil {

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @NotNull T start, @NotNull T end) {
        return value.compareTo(start) >= 0 && value.compareTo(end) < 0;
    }
}
