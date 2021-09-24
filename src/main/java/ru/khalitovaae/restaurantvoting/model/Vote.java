package ru.khalitovaae.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "votes", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"day", "user_id"}, name = "vote_per_day_idx")})
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Vote extends AbstractBaseEntity {

    @Column(name = "day", nullable = false)
    @NotNull
    private LocalDate day;

    @Column(name = "time", nullable = false)
    @NotNull
    private LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    @JsonBackReference
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @NotNull
    @Getter
    @Setter
    @ToString.Exclude
    private Restaurant restaurant;

    public Vote(Integer id, User user, Restaurant restaurant, LocalDate day, LocalTime time) {
        super(id);
        this.user = user;
        this.restaurant = restaurant;
        this.day = day;
        this.time = time;
    }

    public Vote(Integer id, User user, Restaurant restaurant) {
        this(id, user, restaurant, LocalDate.now(), LocalTime.now());
    }
}
