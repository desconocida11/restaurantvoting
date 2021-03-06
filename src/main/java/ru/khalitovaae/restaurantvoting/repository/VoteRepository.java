package ru.khalitovaae.restaurantvoting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.khalitovaae.restaurantvoting.model.Vote;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    Vote getByIdAndUserId(int id, int userId);

    List<Vote> getAllByUserIdOrderByDayDesc(int userId);

    @Query("SELECT v FROM Vote v WHERE v.day = :day AND v.user.id = :userId")
    Vote getByDateAndUserId(LocalDate day, int userId);

    long countAllByRestaurantIdAndDay(int restaurantId, LocalDate day);
}
