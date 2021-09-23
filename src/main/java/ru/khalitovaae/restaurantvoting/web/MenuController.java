package ru.khalitovaae.restaurantvoting.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.khalitovaae.restaurantvoting.model.Dish;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.service.DishService;
import ru.khalitovaae.restaurantvoting.service.RestaurantService;
import ru.khalitovaae.restaurantvoting.service.VoteService;
import ru.khalitovaae.restaurantvoting.to.Menu;
import ru.khalitovaae.restaurantvoting.util.exception.IllegalRequestDataException;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = MenuController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController {

    static final String URL = "/restaurants";

    static final String MENU_URL = "/menus";

    static final String TODAY_URL = "/menus/today";

    private final RestaurantService restaurantService;

    private final DishService dishService;

    private final VoteService voteService;

    public MenuController(RestaurantService restaurantService, DishService dishService, VoteService voteService) {
        this.restaurantService = restaurantService;
        this.dishService = dishService;
        this.voteService = voteService;
    }

    @GetMapping(MENU_URL + "/{date}")
    public List<Restaurant> getAllForDate(@PathVariable LocalDate date) {
        return restaurantService.getAllWithDishesForDate(date);
    }

    @GetMapping(TODAY_URL)
    public List<Restaurant> getAllForToday() {
        return getAllByDate(LocalDate.now());
    }

    @GetMapping("/{id}" + MENU_URL + "/{date}")
    public Restaurant getForDate(@PathVariable int id, @PathVariable LocalDate date) {
        return getByIdAndDay(id, date);
    }

    @GetMapping("/{id}" + TODAY_URL)
    public Restaurant getForToday(@PathVariable int id) {
        return getByIdAndDay(id, LocalDate.now());
    }

    @PostMapping(value = "/{id}" + TODAY_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dish>> createTodayMenu(@PathVariable int id, @RequestBody Menu menu) {
        List<Dish> created = dishService.createFromMenu(menu, id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + "/" + id + MENU_URL)
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{id}" + TODAY_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateTodayMenu(@PathVariable int id, @RequestBody Menu menu) {
        Restaurant restaurant = restaurantService.get(id);
        dishService.updateFromMenu(menu, restaurant);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}" + TODAY_URL)
    public void deleteTodayMenu(@PathVariable int id, @RequestParam(required = false) Integer dish) {
        LocalDate day = LocalDate.now();
        checkUpdateMenu(id, day);
        if (dish == null) {
            dishService.deleteByRestaurantIdAndDate(id, day);
        } else {
            dishService.deleteByIdAndRestaurantId(dish, id);
        }
    }

    private Restaurant getByIdAndDay(int id, LocalDate date) {
        return restaurantService.getByIdWithDishesForDate(id, date);
    }

    private List<Restaurant> getAllByDate(LocalDate date) {
        return restaurantService.getAllWithDishesForDate(date);
    }

    private void checkUpdateMenu(int id, LocalDate day) {
        if (voteService.getCountByRestaurantIdPerDay(id, day) > 0) {
            throw new IllegalRequestDataException("You can't delete today's menu. Votes for the restaurant already exist");
        }
    }
}
