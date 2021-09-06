package ru.khalitovaae.restaurantvoting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import ru.khalitovaae.restaurantvoting.model.Restaurant;
import ru.khalitovaae.restaurantvoting.model.Vote;
import ru.khalitovaae.restaurantvoting.repository.VoteRepository;
import ru.khalitovaae.restaurantvoting.to.VoteResultTo;
import ru.khalitovaae.restaurantvoting.to.VoteTo;
import ru.khalitovaae.restaurantvoting.util.exception.IllegalRequestDataException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.khalitovaae.restaurantvoting.util.DateTimeUtil.isBetweenHalfOpen;
import static ru.khalitovaae.restaurantvoting.util.ValidationUtil.*;

@Service
public class VoteService {
    private final VoteRepository repository;

    @Value("${vote.deadline}")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime deadline;

    private final Clock clock;

    public VoteService(VoteRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Vote create(Vote vote, int userId) {
        Vote existed = getExisted(LocalDate.now(), userId);
        if (existed != null) {
            throw new IllegalRequestDataException("Vote already exists for userId=" + userId);
        }
        return repository.save(vote);
    }

    public void update(Vote vote, int userId) {
        Vote existed = getExisted(LocalDate.now(clock), userId);
        checkIllegalRequestWithId(existed == null, "Vote doesn't exist for userId=", userId);
        checkIllegalRequest(vote.id() != existed.id(), "Inconsistency in votes' ids");
        repository.save(vote);
    }

    public void updateFromTo(VoteTo voteTo, int userId) {
        Vote existed = getExisted(LocalDate.now(clock), userId);
        checkIllegalRequestWithId(existed == null, "Vote doesn't exist for userId=", userId);
        checkIllegalRequest(voteTo.id() != existed.id(), "Inconsistency in votes' ids");
        existed.setRestaurant(voteTo.getRestaurant());
        existed.setDay(voteTo.getDayTime().toLocalDate());
        existed.setTime(voteTo.getDayTime().toLocalTime());
        repository.save(existed);
    }

    public void delete(int id, int userId) {
        Vote existed = repository.getByIdAndUserId(id, userId);
        checkIllegalRequestWithId(existed == null, "Vote doesn't exist for userId=", userId);
        if (getExisted(LocalDate.now(clock), userId) != null) {
            repository.delete(id, userId);
        }
    }

    public Vote get(int id, int userId) {
        Vote vote = repository.getByIdAndUserId(id, userId);
        notFound(vote == null, "Vote with id=" + id + " doesn't exist for userId=" + userId);
        return vote;
    }

    public List<Vote> getByUser(int userId){
        return repository.getAllByUserIdOrderByDayDesc(userId);
    }

    private Vote getExisted(LocalDate day, int userId) {
        Vote existed = repository.getByDateAndUserId(day, userId);
        checkIllegalRequest(existed != null && !isVotingOpen(), "Voting is closed for today, you can't change the vote");
        return existed;
    }

    public long getCountByRestaurantIdPerDay(int restaurantId, LocalDate day) {
        return repository.countAllByRestaurantIdAndDay(restaurantId, day);
    }

    public List<VoteResultTo> getVotingResults(LocalDate day) {
        List<VoteResultTo> votes = new ArrayList<>();
        final Map<Restaurant, Long> collect = repository.getByDate(day).stream()
                .collect(Collectors.groupingBy(Vote::getRestaurant, Collectors.summingLong(value -> 1)));
        collect.forEach((restaurant, result) -> votes.add(new VoteResultTo(restaurant, result)));
        return Collections.unmodifiableList(votes);
    }

    private boolean isVotingOpen() {
        LocalDateTime dateTime = LocalDateTime.now(clock);
        return dateTime.toLocalDate().equals(LocalDate.now(clock)) && isBetweenHalfOpen(dateTime.toLocalTime(), LocalTime.MIN, deadline);
    }
}
