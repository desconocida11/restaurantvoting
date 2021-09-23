package ru.khalitovaae.restaurantvoting.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.khalitovaae.restaurantvoting.model.Restaurant;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class VoteTo extends BaseTo{

    @NotNull
    private LocalDateTime dayTime;

    @NotNull
    private Restaurant restaurant;

    public VoteTo(Integer id, Restaurant restaurant, LocalDateTime dayTime) {
        super(id);
        this.restaurant = restaurant;
        this.dayTime = dayTime;
    }
}
