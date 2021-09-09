package ru.khalitovaae.restaurantvoting.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.khalitovaae.restaurantvoting.model.Dish;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.repository.DishRepository;
import ru.khalitovaae.restaurantvoting.repository.RestaurantRepository;
import ru.khalitovaae.restaurantvoting.to.Menu;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.notFound;

@Service
public class DishService {
    private final DishRepository repository;
    private final RestaurantRepository restaurantRepository;

    public DishService(DishRepository dishRepository, RestaurantRepository restaurantRepository) {
        this.repository = dishRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Dish create(Dish dish) {
        Assert.notNull(dish, "dish must not be null");
        return repository.save(dish);
    }

    public List<Dish> createFromMenu(Menu menu, int restaurantId) {
        Assert.notNull(menu, "menu must not be null");
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        List<Dish> dishList = menu.getDishes()
                .stream()
                .map(dishTo -> new Dish(null, dishTo.getName(), menu.getDay(), dishTo.getPrice(), restaurant))
                .collect(Collectors.toList());
        return repository.saveAll(dishList);
    }

    public List<Dish> updateFromMenu(Menu menu, Restaurant restaurant) {
        Assert.notNull(menu, "menu must not be null");
        Assert.notNull(restaurant, "restaurant must not be null");
        List<Dish> dishList = menu.getDishes()
                .stream()
                .map(dishTo -> new Dish(dishTo.id(), dishTo.getName(), menu.getDay(), dishTo.getPrice(), restaurant))
                .collect(Collectors.toList());
        return repository.saveAll(dishList);
    }

    public void delete(int id) {
        notFound(repository.delete(id) != 1, "Dish with id=" + id + " not found");
    }

    public void deleteByIdAndRestaurantId(int id, int restaurant) {
        notFound(repository.deleteByIdAndRestaurantId(id, restaurant) != 1, "Dish with id=" + id + " not found in restaurant id=" + restaurant);
    }

    public void deleteByRestaurantIdAndDate(int id, LocalDate day) {
        notFound(repository.deleteAllByRestaurantIdAndDay(id, day) < 1, "Dishes for restaurant id=" + id + " and day=" + day + " not found");
    }

    public Dish get(int id) {
        Dish dish = repository.findById(id).orElse(null);
        notFound(dish == null, "Dish with id=" + id + " not found");
        return dish;
    }

    public void update(Dish dish) {
        Assert.notNull(dish, "dish must not be null");
        repository.save(dish);
    }
}