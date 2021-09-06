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
import ru.khalitovaae.restaurantvoting.to.VoteResultTo;
import ru.khalitovaae.restaurantvoting.util.exception.IllegalRequestDataException;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.*;

@RestController
@RequestMapping(value = RestaurantController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {

    static final String URL = "/restaurants";

    static final String MENU_URL = "/menus";

    static final String VOTES_URL = "/votes";

    private final RestaurantService restaurantService;

    private final DishService dishService;

    private final VoteService voteService;

    public RestaurantController(RestaurantService restaurantService, DishService dishService, VoteService voteService) {
        this.restaurantService = restaurantService;
        this.dishService = dishService;
        this.voteService = voteService;
    }

    @GetMapping(MENU_URL)
    public List<Restaurant> getAllMenusForDate(@RequestParam(required = false) LocalDate date) {
        return restaurantService.getAllWithDishesForDate(getDay(date));
    }

    @GetMapping
    public List<Restaurant> getAll() {
        return restaurantService.getAll();
    }

    @GetMapping(VOTES_URL)
    public List<VoteResultTo> getVotingResults(@RequestParam(required = false) LocalDate date) {
        return voteService.getVotingResults(getDay(date));
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        return restaurantService.get(id);
    }

    @GetMapping("/{id}" + VOTES_URL)
    public long getVotes(@PathVariable int id, @RequestParam(required = false) LocalDate date) {
        return voteService.getCountByRestaurantIdPerDay(id, getDay(date));
    }

    @GetMapping("/{id}" + MENU_URL)
    public Restaurant getMenuForDate(@PathVariable int id,
                                     @RequestParam(required = false) LocalDate date) {
        return restaurantService.getByIdWithDishesForDate(id, getDay(date));
    }

    @GetMapping("/search")
    public Restaurant getByName(@RequestParam String name) {
        return restaurantService.getByName(name);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        restaurantService.delete(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @Valid @RequestBody Restaurant restaurant) {
        assureIdConsistent(restaurant, id);
        restaurantService.update(restaurant);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createWithLocation(@Valid @RequestBody Restaurant restaurant) {
        checkNew(restaurant);
        Restaurant created = restaurantService.create(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PostMapping(value = "/{id}" + MENU_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dish>> createTodayMenu(@PathVariable int id, @Valid @RequestBody Menu menu) {
        LocalDate day = LocalDate.now();
        assureDayConsistent(menu, day);
        List<Dish> created = dishService.createFromMenu(menu, id);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + id + MENU_URL)
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}" + MENU_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dish>> updateTodayMenu(@PathVariable int id, @Valid @RequestBody Menu menu) {
        LocalDate day = LocalDate.now();
        assureDayConsistent(menu, day);
        Restaurant restaurant = restaurantService.get(id);
        List<Dish> updated = dishService.updateFromMenu(menu, restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + id + MENU_URL)
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}" + MENU_URL)
    public void deleteTodayMenu(@PathVariable int id) {
        LocalDate day = LocalDate.now();
        checkUpdateMenu(id, day);
        dishService.deleteByRestaurantIdAndDate(id, day);
    }

//    CHECK defaultValue = "#{T(java.time.LocalDate).now()}" SpEL TODO
    private LocalDate getDay(LocalDate date) {
        return date == null ? LocalDate.now() : date;
    }

    private void checkUpdateMenu(int id, LocalDate day) {
        if (voteService.getCountByRestaurantIdPerDay(id, day) > 0) {
            throw new IllegalRequestDataException("You can't delete today's menu. Votes for the restaurant already exist");
        }
    }
}
