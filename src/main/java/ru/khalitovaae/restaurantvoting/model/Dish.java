package ru.khalitovaae.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "dishes")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Dish extends AbstractNamedEntity {

    @Column(name = "price", nullable = false)
    @Range(min = 10)
    private long price;

    @Column(name = "day", nullable = false)
    @NotNull
    private LocalDate day;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
//    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    private Restaurant restaurant;

    public Dish(Integer id, String name, LocalDate day, long price, Restaurant restaurant) {
        super(id, name);
        this.price = price;
        this.day = day;
        this.restaurant = restaurant;
    }

    public Dish(Integer id, String name, long price, Restaurant restaurant) {
        super(id, name);
        this.price = price;
        this.day = LocalDate.now();
        this.restaurant = restaurant;
    }

    public Dish(Dish d) {
        this(d.getId(), d.getName(), d.getDay(), d.getPrice(), d.getRestaurant());
    }
}