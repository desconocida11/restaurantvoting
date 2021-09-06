package ru.khalitovaae.restaurantvoting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.khalitovaae.restaurantvoting.model.Dish;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface DishRepository extends JpaRepository<Dish, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id=:id")
    int delete(@Param("id") int id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.restaurant.id=:id AND d.day=:day")
    int deleteAllByRestaurantIdAndDay(@Param("id") int id, @Param("day") LocalDate day);

    List<Dish> findAllByDay(LocalDate date);

    List<Dish> findAllByRestaurantId(int restaurantId);
}