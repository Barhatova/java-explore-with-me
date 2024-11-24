package ru.yandex.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id IN (:users) " +
            "AND e.state IN (:states) " +
            "AND e.category.id IN (:categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> getEventsByParams(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    List<Event> getAllByInitiatorId(Long userId, Pageable pageable);

    Event getByInitiatorIdAndId(Long userId, Long eventId);

    List<Event> getByIdIn(List<Long> ids);

    Event getAllByCategoryId(Long catId);

    List<Event> getAllByIdIn(List<Long> ids);

    Optional<Event> getByIdAndState(Long id, EventState state);
}
