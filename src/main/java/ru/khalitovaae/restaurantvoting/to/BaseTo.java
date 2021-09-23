package ru.khalitovaae.restaurantvoting.to;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.khalitovaae.restaurantvoting.HasId;


@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class BaseTo implements HasId {
    protected Integer id;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}