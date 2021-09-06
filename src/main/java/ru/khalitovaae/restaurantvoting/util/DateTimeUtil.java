package ru.khalitovaae.restaurantvoting.util;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtil {

    public static <T extends Comparable<T>> boolean isBetweenHalfOpen(T value, @NotNull T start, @NotNull T end) {
        return value.compareTo(start) >= 0 && value.compareTo(end) < 0;
    }
}
