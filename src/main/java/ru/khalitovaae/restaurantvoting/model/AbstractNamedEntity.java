package ru.khalitovaae.restaurantvoting.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.khalitovaae.restaurantvoting.util.validation.NoHtml;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNamedEntity extends AbstractBaseEntity {

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "name", nullable = false)
    @Getter
    @Setter
    @NoHtml  // https://stackoverflow.com/questions/17480809
    protected String name;

    protected AbstractNamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + '(' + name + ')';
    }
}