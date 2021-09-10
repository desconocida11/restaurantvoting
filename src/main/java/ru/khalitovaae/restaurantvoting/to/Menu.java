package ru.khalitovaae.restaurantvoting.to;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class Menu {

    @Getter
    @Setter
    private LocalDate day;

    @Getter
    @Setter
    private List<DishTo> dishes;

    @Override
    public String toString() {
        return "Menu{" +
                "day=" + day +
                ", dishes=" + dishes +
                '}';
    }
}
