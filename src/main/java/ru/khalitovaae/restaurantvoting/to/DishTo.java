package ru.khalitovaae.restaurantvoting.to;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import ru.khalitovaae.restaurantvoting.model.Dish;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
public class DishTo extends BaseTo {

    @Getter
    @Setter
    @Range(min = 1L)
    private long price;

    @NotBlank
    @Size(min = 2, max = 100)
    @Getter
    @Setter
    private String name;

    public DishTo(Integer id, String name, long price) {
        super(id);
        this.name = name;
        this.price = price;
    }

    public DishTo(Dish dish) {
        this(dish.getId(), dish.getName(), dish.getPrice());
    }

    @Override
    public String toString() {
        return "Dish " + id + " {" +
                "name=" + name +
                ", price=" + price +
                '}';
    }
}