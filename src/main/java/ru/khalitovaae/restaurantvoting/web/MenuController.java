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

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.DateTimeUtil.getDay;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.assureDayConsistent;

@RestController
@RequestMapping(value = MenuController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController {
    static final String URL = "/menus";

    private final RestaurantService restaurantService;

    private final DishService dishService;

    private final VoteService voteService;

    public MenuController(RestaurantService restaurantService, DishService dishService, VoteService voteService) {
        this.restaurantService = restaurantService;
        this.dishService = dishService;
        this.voteService = voteService;
    }

    @GetMapping
    public List<Restaurant> getMenuForDate(@RequestParam(required = false) Integer restaurant,
                                           @RequestParam(required = false) LocalDate date) {
        if (restaurant == null)  {
            return restaurantService.getAllWithDishesForDate(getDay(date));
        }
        return List.of(restaurantService.getByIdWithDishesForDate(restaurant, getDay(date)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dish>> createTodayMenu(@RequestParam int restaurant,
                                                      @Valid @RequestBody Menu menu) {
        LocalDate day = LocalDate.now();
        assureDayConsistent(menu, day);
        List<Dish> created = dishService.createFromMenu(menu, restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + "?restaurant=" + restaurant)
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Dish>> updateTodayMenu(@RequestParam int restaurant,
                                                      @Valid @RequestBody Menu menu) {
        LocalDate day = LocalDate.now();
        assureDayConsistent(menu, day);
        Restaurant restaurantObj = restaurantService.get(restaurant);
        List<Dish> updated = dishService.updateFromMenu(menu, restaurantObj);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + "?restaurant=" + restaurant)
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteTodayMenu(@RequestParam int restaurant,
                                @RequestParam(required = false) Integer dish) {
        LocalDate day = LocalDate.now();
        checkUpdateMenu(restaurant, day);
        if (dish == null) {
            dishService.deleteByRestaurantIdAndDate(restaurant, day);
        } else {
            dishService.deleteByIdAndRestaurantId(dish, restaurant);
        }
    }

    private void checkUpdateMenu(int id, LocalDate day) {
        if (voteService.getCountByRestaurantIdPerDay(id, day) > 0) {
            throw new IllegalRequestDataException("You can't delete today's menu. Votes for the restaurant already exist");
        }
    }
}
