package ru.khalitovaae.restaurantvoting.util;

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@UtilityClass
public class DateTimeUtil {

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @NotNull T start, @NotNull T end) {
        return value.compareTo(start) >= 0 && value.compareTo(end) < 0;
    }

    //    CHECK defaultValue = "#{T(java.time.LocalDate).now()}" SpEL TODO
    public static LocalDate getDay(LocalDate date) {
        return date == null ? LocalDate.now() : date;
    }
}
