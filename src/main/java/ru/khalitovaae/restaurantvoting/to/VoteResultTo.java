package ru.khalitovaae.restaurantvoting.to;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.khalitovaae.restaurantvoting.model.Restaurant;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
public class VoteResultTo {

    @Getter
    @Setter
    @NotNull
    private Restaurant restaurant;

    @Getter
    @Setter
    @NotNull
    private Long result;

    @Override
    public String toString() {
        return "VoteResultTo{" +
                "restaurant=" + restaurant +
                ", result=" + result +
                '}';
    }
}
