package ru.khalitovaae.restaurantvoting.web;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.SecurityUtil.authUserId;

@RestController
@RequestMapping(value = VoteController.URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteController {

    static final String URL = "/votes";

    private final VoteService service;

    private final RestaurantService restaurantService;

    private final Clock clock;

    @Autowired
    public VoteController(VoteService service, RestaurantService restaurantService, Clock clock) {
        this.service = service;
        this.restaurantService = restaurantService;
        this.clock = clock;
    }

    @GetMapping
    public List<VoteTo> getAll() {
        int userId = authUserId();
        return service.getByUser(userId);
    }

    @GetMapping("/{id}")
    public VoteTo get(@PathVariable int id) {
        int userId = authUserId();
        return service.get(id, userId);
    }

    @GetMapping("/today")
    public VoteTo getTodayVote() {
        return service.getTodayVote(authUserId());
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("id") int id, @RequestParam int restaurant) {
        VoteTo voteTo = new VoteTo(id, restaurant, LocalDate.now(clock), LocalTime.now(clock));
        service.updateFromTo(voteTo, authUserId());
    }

    @PostMapping
    public ResponseEntity<Vote> createWithLocation(@RequestParam int restaurant) {
        Restaurant getRestaurant = restaurantService.getByIdWithDishesForDate(restaurant, LocalDate.now(clock));
        Vote vote = new Vote(null, SecurityUtil.get().getUser(), getRestaurant, LocalDate.now(clock), LocalTime.now(clock));
        Vote created = service.create(vote);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }
}
