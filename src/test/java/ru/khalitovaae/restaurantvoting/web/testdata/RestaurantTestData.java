package ru.khalitovaae.restaurantvoting.web.testdata;

import lombok.experimental.UtilityClass;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.to.DishTo;
import ru.khalitovaae.restaurantvoting.to.Menu;
import ru.khalitovaae.restaurantvoting.web.MatcherFactory;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.khalitovaae.restaurantvoting.web.testdata.DishTestData.*;

@UtilityClass
public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class,
                    "dishes");

    public static MatcherFactory.Matcher<Restaurant> WITH_DISHES_MATCHER =
            MatcherFactory.usingAssertions(Restaurant.class,
//     No need use ignoringAllOverriddenEquals, see https://assertj.github.io/doc/#breaking-changes
                    (a, e) -> assertThat(a).usingRecursiveComparison()
                            .ignoringFields("dishes.restaurant").isEqualTo(e),
                    (a, e) -> {
                        throw new UnsupportedOperationException();
                    });

    public static final int METROPOL_ID = 4;
    public static final int PUSHKIN_ID = 5;
    public static final int NAPOLIPIZZA_ID = 6;
    public static final int DUMMY_ID = 23;
    public static final String METROPOL = "Metropol";
    public static final String PUSHKIN = "Pushkin";
    public static final String NAPOLIPIZZA = "NapoliPizza";
    public static final String DUMMY_RESTAURANT = "Dummy Restaurant";

    public static final Restaurant metropol = new Restaurant(METROPOL_ID, METROPOL);
    public static final Restaurant pushkin = new Restaurant(PUSHKIN_ID, PUSHKIN);
    public static final Restaurant napolipizza = new Restaurant(NAPOLIPIZZA_ID, NAPOLIPIZZA);
    public static final Restaurant dummyrestaurant = new Restaurant(DUMMY_ID, DUMMY_RESTAURANT);

    public static void setDishes() {
        metropol.setDishes(List.of(metropolDish1, metropolDish2));
        pushkin.setDishes(List.of(pushkinDish1, pushkinDish2, pushkinDish3, pushkinDish4, pushkinDish5));
        napolipizza.setDishes(List.of(pizzaDish1, pizzaDish2, pizzaDish3));
    }

    public static Restaurant getNew() {
        return new Restaurant(null, "New Restaurant");
    }

    public static Restaurant getUpdated() {
        Restaurant updated = new Restaurant(metropol);
        updated.setName("Metropol Updated");
        return updated;
    }

    public static List<Restaurant> getAllSorted() {
        return List.of(dummyrestaurant, metropol, napolipizza, pushkin);
    }

    public static Restaurant getMenuForToday() {
        Restaurant restaurant = new Restaurant(metropol);
        restaurant.setDishes(List.of(metropolDish1, metropolDish2));
        return restaurant;
    }

    public static Menu createNewMenu() {
        Menu menu = new Menu();
        menu.setDay(LocalDate.now());
        List<DishTo> dishes = getDishesToForMenu();
        menu.setDishes(dishes);
        return menu;
    }
}
