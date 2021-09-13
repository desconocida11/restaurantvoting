package ru.khalitovaae.restaurantvoting.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.notFound;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.notFoundWithId;

@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository repository;

    public Restaurant create(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        return repository.save(restaurant);
    }

    @CacheEvict(value = "restaurants", key = "#id")
    public void delete(int id) {
        notFoundWithId(repository.delete(id) != 1, "Not found restaurant with id=", id);
    }

    public Restaurant get(int id) {
        Restaurant restaurant = repository.findById(id).orElse(null);
        notFoundWithId(restaurant == null, "Not found restaurant with id=", id);
        return restaurant;
    }

    public Restaurant getByName(String name) {
        Assert.notNull(name, "name must not be null");
        Restaurant restaurant = repository.getByName(name);
        notFound(restaurant == null, "Not found restaurant name containing " + name);
        return restaurant;
    }

    public List<Restaurant> getAll() {
        return repository.findAll(repository.SORT_NAME);
    }

    public List<Restaurant> getAllWithDishesForDate(LocalDate date) {
        List<Restaurant> restaurants = repository.getAllWithDishes(date);
        notFound(restaurants == null, "Menus for " + date + " are not available");
        return restaurants;
    }

    @Cacheable(value = "restaurants", key = "#id", condition = "#date eq T(java.time.LocalDate).now()")
    public Restaurant getByIdWithDishesForDate(int id, LocalDate date) {
        Restaurant restaurant = repository.getByIdWithDishes(id, date);
        notFound(restaurant == null || restaurant.getDishes() == null, "Restaurant id=" + id + " doesn't exist or didn't provide menu for " + date);
        return restaurant;
    }

    @CacheEvict(value = "restaurants", key = "#restaurant.id()")
    public void update(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        repository.save(restaurant);
    }
}