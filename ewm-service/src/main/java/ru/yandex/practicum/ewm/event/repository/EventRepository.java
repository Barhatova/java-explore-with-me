package ru.yandex.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.user.model.User;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByInitiator(User initiator, Pageable pageable);

    Optional<Event> findEventByIdAndInitiator(Long eventId, User initiator);
}
