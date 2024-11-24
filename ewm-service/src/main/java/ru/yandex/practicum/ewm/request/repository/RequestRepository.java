package ru.yandex.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> getAllByRequesterIdAndEventId(Long eventId, Long userId);

    List<Request> getAllByRequesterId(Long userId);

    List<Request> getAllByEventId(Long eventId);

    List<Request> getByIdIn(List<Long> ids);
}
