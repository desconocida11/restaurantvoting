package ru.khalitovaae.restaurantvoting.web.testdata;

import lombok.experimental.UtilityClass;
import ru.khalitovaae.restaurantvoting.model.Dish;
import ru.khalitovaae.restaurantvoting.to.DishTo;
import ru.khalitovaae.restaurantvoting.to.Menu;
import ru.khalitovaae.restaurantvoting.web.MatcherFactory;

import java.time.LocalDate;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.web.testdata.RestaurantTestData.*;

@UtilityClass
public class DishTestData {

    public static final MatcherFactory.Matcher<Dish> DISH_MATCHER =
            MatcherFactory.usingIgnoringFieldsComparator(Dish.class,
                    "id", "restaurant");

    public static final Dish metropolDish1;
    public static final Dish metropolDish2;

    public static final Dish pushkinDish1;
    public static final Dish pushkinDish2;
    public static final Dish pushkinDish3;
    public static final Dish pushkinDish4;
    public static final Dish pushkinDish5;

    public static final Dish pizzaDish1;
    public static final Dish pizzaDish2;
    public static final Dish pizzaDish3;

    static {
        metropolDish1 = new Dish(7, "Smashed potatoes with steak", 12000, metropol);
        metropolDish2 = new Dish(8, "Cheesecake", 5000, metropol);

        pushkinDish1 = new Dish(9, "Borsch with bread", 8000, pushkin);
        pushkinDish2 = new Dish(10, "Pumpkin Cake", 4000, pushkin);
        pushkinDish3 = new Dish(11, "Chicken Curry", 6000, pushkin);
        pushkinDish4 = new Dish(12, "Beef with veggies", 10000, pushkin);
        pushkinDish5 = new Dish(13, "Grilled vegetables", 7000, pushkin);

        pizzaDish1 = new Dish(14, "Spinach mousse", 3000, napolipizza);
        pizzaDish2 = new Dish(15, "Milkshake", 200, napolipizza);
        pizzaDish3 = new Dish(16, "Pancakes", 400, napolipizza);
    }

    public static List<DishTo> getDishesToForMenu() {
        return List.of(new DishTo(null, "Dish1", 1000),
                new DishTo(null, "Dish2", 2000));
    }

    public static List<Dish> getDishesForMenu() {
        return List.of(new Dish(null, "Dish1", LocalDate.now(), 1000, dummyrestaurant),
                new Dish(null, "Dish2", LocalDate.now(), 2000, dummyrestaurant));
    }

    public static List<Integer> getMenuPushkin() {
        return List.of(9, 10, 11, 12, 13);
    }

    public static Menu getUpdatedMenu() {
        Menu menu = new Menu();
        List<DishTo> dishes = List.of(new DishTo(pizzaDish1), new DishTo(pizzaDish2), new DishTo(pizzaDish3));
        dishes.forEach(dishTo -> {
            dishTo.setName(dishTo.getName() + " updated");
            dishTo.setPrice(1000);
        });
        menu.setDishes(dishes);
        return menu;
    }

    public static List<Dish> getUpdatedDishes() {
        List<Dish> dishes = List.of(new Dish(pizzaDish1), new Dish(pizzaDish2), new Dish(pizzaDish3));
        dishes.forEach(dish -> {
            dish.setName(dish.getName() + " updated");
            dish.setPrice(1000);
        });
        return dishes;
    }

}
