package ru.khalitovaae.restaurantvoting.web;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.service.RestaurantService;
import ru.khalitovaae.restaurantvoting.service.VoteService;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.assureIdConsistent;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = RestaurantController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {

    static final String URL = "/restaurants";

    static final String VOTES_URL = "/votes";

    private final RestaurantService restaurantService;

    private final VoteService voteService;

    public RestaurantController(RestaurantService restaurantService, VoteService voteService) {
        this.restaurantService = restaurantService;
        this.voteService = voteService;
    }

    @GetMapping
    public List<Restaurant> getAll() {
        return restaurantService.getAll();
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        return restaurantService.get(id);
    }

    @GetMapping("/{id}" + VOTES_URL + "/{date}")
    public long getVotes(@PathVariable int id, @PathVariable LocalDate date) {
        return voteService.getCountByRestaurantIdPerDay(id, date);
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
}
