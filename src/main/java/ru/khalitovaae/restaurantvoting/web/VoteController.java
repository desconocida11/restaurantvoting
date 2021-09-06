package ru.khalitovaae.restaurantvoting.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.model.Vote;
import ru.khalitovaae.restaurantvoting.service.RestaurantService;
import ru.khalitovaae.restaurantvoting.service.VoteService;
import ru.khalitovaae.restaurantvoting.to.VoteTo;
import ru.khalitovaae.restaurantvoting.util.SecurityUtil;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.SecurityUtil.authUserId;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.assureIdConsistent;

@RestController
@RequestMapping(value = VoteController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteController {

    static final String URL = "/votes";

    private final VoteService service;

    private final RestaurantService restaurantService;

    public VoteController(VoteService service, RestaurantService restaurantService) {
        this.service = service;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<Vote> getAll() {
        int userId = authUserId();
        return service.getByUser(userId);
    }

    @GetMapping("/{id}")
    public Vote get(@PathVariable int id) {
        int userId = authUserId();
        return service.get(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        int userId = authUserId();
        service.delete(id, userId);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") int id, @Valid @RequestBody VoteTo voteTo) {
        assureIdConsistent(voteTo, id);
        service.updateFromTo(voteTo, authUserId());
    }

    @PostMapping
    public ResponseEntity<Vote> createWithLocation(@RequestParam int restaurant) {
        Restaurant getRestaurant = restaurantService.getByIdWithDishesForDate(restaurant, LocalDate.now());
        Vote vote = new Vote(null, SecurityUtil.get().getUser(), getRestaurant);
        Vote created = service.create(vote, authUserId());
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}
