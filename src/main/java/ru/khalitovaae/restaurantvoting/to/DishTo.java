package ru.khalitovaae.restaurantvoting.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import ru.khalitovaae.restaurantvoting.model.Dish;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class DishTo extends BaseTo {

    @Range(min = 1L)
    private long price;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    public DishTo(Integer id, String name, long price) {
        super(id);
        this.name = name;
        this.price = price;
    }

    public DishTo(Dish dish) {
        this(dish.getId(), dish.getName(), dish.getPrice());
    }
}