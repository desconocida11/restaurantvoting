package ru.khalitovaae.restaurantvoting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.khalitovaae.restaurantvoting.model.Vote;
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

    @Transactional
    public Vote create(Vote vote) {
        return repository.save(vote);
    }

    @Transactional
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

    public Vote getTodayVote(int userId) {
        Vote vote = repository.getByDateAndUserId(LocalDate.now(clock), userId);
        notFound(vote == null, "Today's vote doesn't exist for userId=" + userId);
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

    private boolean isVotingOpen() {
        LocalDateTime dateTime = LocalDateTime.now(clock);
        return dateTime.toLocalDate().equals(LocalDate.now(clock)) && isBetweenHalfOpen(dateTime.toLocalTime(), LocalTime.MIN, deadline);
    }
}
