package ru.khalitovaae.restaurantvoting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.model.Vote;
import ru.khalitovaae.restaurantvoting.repository.RestaurantRepository;
import ru.khalitovaae.restaurantvoting.repository.VoteRepository;
import ru.khalitovaae.restaurantvoting.to.VoteTo;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.khalitovaae.restaurantvoting.util.DateTimeUtil.isBetweenHalfOpen;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.*;

@Service
@Transactional(readOnly = true)
public class VoteService {
    private final VoteRepository repository;

    private final RestaurantRepository restaurantRepository;

    @Value("${vote.deadline}")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime deadline;

    private final Clock clock;

    public VoteService(VoteRepository repository, RestaurantRepository restaurantRepository, Clock clock) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
        this.clock = clock;
    }

    @Transactional
    public Vote create(Vote vote) {
        return repository.save(vote);
    }

    @Transactional
    public void updateFromTo(VoteTo voteTo, int userId) {
        checkIllegalRequest(!isVotingOpen(), "Voting is closed for today, you can't change the vote");
        Vote existed = repository.getByDateAndUserId(LocalDate.now(clock), userId);
        checkIllegalRequestWithId(existed == null, "Vote doesn't exist for userId=", userId);
        checkIllegalRequest(voteTo.id() != existed.id(), "Inconsistency in votes' ids");
        Restaurant getRestaurant = restaurantRepository.getByIdWithDishes(voteTo.getRestaurantId(), LocalDate.now(clock));
        existed.setRestaurant(getRestaurant);
        existed.setDay(voteTo.getDay());
        existed.setTime(voteTo.getTime());
        repository.save(existed);
    }

    public VoteTo get(int id, int userId) {
        Vote vote = repository.getByIdAndUserId(id, userId);
        notFound(vote == null, "Vote with id=" + id + " doesn't exist for userId=" + userId);
        return new VoteTo(vote);
    }

    public VoteTo getTodayVote(int userId) {
        Vote vote = repository.getByDateAndUserId(LocalDate.now(clock), userId);
        notFound(vote == null, "Today's vote doesn't exist for userId=" + userId);
        return new VoteTo(vote);
    }

    public List<VoteTo> getByUser(int userId){
        final List<Vote> votes = repository.getAllByUserIdOrderByDayDesc(userId);
        return votes.stream().map(VoteTo::new).toList();
    }

    public long getCountByRestaurantIdPerDay(int restaurantId, LocalDate day) {
        return repository.countAllByRestaurantIdAndDay(restaurantId, day);
    }

    private boolean isVotingOpen() {
        LocalDateTime dateTime = LocalDateTime.now(clock);
        return isBetweenHalfOpen(dateTime.toLocalTime(), LocalTime.MIN, deadline);
    }
}
