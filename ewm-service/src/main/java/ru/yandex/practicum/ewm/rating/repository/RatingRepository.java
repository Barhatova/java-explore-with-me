package ru.yandex.practicum.ewm.rating.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.rating.model.Rating;
import ru.yandex.practicum.ewm.user.model.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("select AVG(r.rating) " +
            "from Rating r " +
            "where r.event = :event")
    Double avgRatingByEvent(Event event);

    @Query("select AVG(r.rating) " +
            "from Rating r " +
            "where r.user = :user")
    Double avgRatingByUser(User user);

    Page<Rating> findAllByUser(User user, Pageable pageable);

    Page<Rating> findAllByEvent(Event event, Pageable pageable);

    Boolean existsByIdAndUser(Long ratingId, User user);
}