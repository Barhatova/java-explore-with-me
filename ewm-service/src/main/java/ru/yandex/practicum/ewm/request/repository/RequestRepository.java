package ru.yandex.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.request.model.ParticipationRequest;
import ru.yandex.practicum.ewm.request.model.RequestStatus;
import ru.yandex.practicum.ewm.user.model.User;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findAllByEvent(Event event);

    Collection<ParticipationRequest> findAllByEventAndStatus(Event event, RequestStatus status);

    Long countByEventAndStatus(Event event, RequestStatus status);

    Collection<ParticipationRequest> findAllByRequester(User user);
}
