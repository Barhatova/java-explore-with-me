package ru.yandex.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.model.SortType;
import ru.yandex.practicum.ewm.event.model.State;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  SortType sort,
                                  Integer from,
                                  Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    List<EventFullDto> getFullEvents(List<Long> users,
                                     List<State> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Integer from,
                                     Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEventsByCurrentUser(Long userId, int from, int size);


    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto getFullEventByIdForCurrentUser(Long userId, Long eventId);

    EventFullDto updateByCurrentUser(Long userId,
                                     Long eventId,
                                     UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId,
                                                Long eventId,
                                                EventRequestStatusUpdateRequest statusUpdateRequest);
}