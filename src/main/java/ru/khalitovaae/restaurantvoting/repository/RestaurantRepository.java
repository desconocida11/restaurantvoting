package ru.khalitovaae.restaurantvoting.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.khalitovaae.restaurantvoting.model.Restaurant;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Sort SORT_NAME = Sort.by(Sort.Direction.ASC, "name");

    @Transactional
    @Modifying
    @Query("DELETE FROM Restaurant r WHERE r.id=:id")
    int delete(@Param("id") int id);

    Restaurant getByName(String name);

    @EntityGraph(attributePaths = { "dishes" })
    @Query("SELECT r FROM Restaurant r JOIN r.dishes d WHERE d.day=:day ORDER BY r.name ASC")
    List<Restaurant> getAllWithDishes(@Param("day") LocalDate day);

//    @EntityGraph(attributePaths = { "dishes" })
    @Query("SELECT r FROM Restaurant r LEFT JOIN FETCH r.dishes d WHERE d.day=:day AND r.id=:id")
    Restaurant getByIdWithDishes(@Param("id") int id, @Param("day") LocalDate day);
}
