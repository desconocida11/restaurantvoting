package ru.khalitovaae.restaurantvoting.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.khalitovaae.restaurantvoting.model.Restaurant;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
public class VoteTo extends BaseTo{

    @NotNull
    @Getter
    @Setter
    private LocalDateTime dayTime;

    @NotNull
    @Getter
    @Setter
    private Restaurant restaurant;

    public VoteTo(Integer id, Restaurant restaurant, LocalDateTime dayTime) {
        super(id);
        this.restaurant = restaurant;
        this.dayTime = dayTime;
    }

    @Override
    public String toString() {
        return "VoteTo{" +
                "dayTime=" + dayTime +
                ", restaurant=" + restaurant +
                '}';
    }
}
