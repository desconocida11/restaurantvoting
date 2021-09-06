package ru.khalitovaae.restaurantvoting.to;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.khalitovaae.restaurantvoting.HasId;


@NoArgsConstructor
@AllArgsConstructor
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