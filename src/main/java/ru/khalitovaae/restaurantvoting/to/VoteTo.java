package ru.khalitovaae.restaurantvoting.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.khalitovaae.restaurantvoting.model.Vote;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class VoteTo extends BaseTo{

    @NotNull
    private LocalDate day;

    @NotNull
    private LocalTime time;

    @NotNull
    private Integer restaurantId;

    public VoteTo(Integer id, Integer restaurantId, LocalDate day, LocalTime time) {
        super(id);
        this.restaurantId = restaurantId;
        this.day = day;
        this.time = time;
    }

    public VoteTo(Vote vote) {
        this.id = vote.getId();
        this.restaurantId = vote.getRestaurant().getId();
        this.day = vote.getDay();
        this.time = vote.getTime();
    }
}
