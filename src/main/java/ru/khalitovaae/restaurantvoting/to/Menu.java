package ru.khalitovaae.restaurantvoting.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class Menu {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate day = LocalDate.now();

    private List<DishTo> dishes;
}
