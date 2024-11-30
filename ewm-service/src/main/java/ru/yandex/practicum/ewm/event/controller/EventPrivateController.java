package ru.yandex.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.event.dto.*;
import ru.yandex.practicum.ewm.event.service.EventService;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{user-id}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByCurrentUser(@PathVariable("user-id") Long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByCurrentUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable("user-id") Long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/{event-id}")
    public EventFullDto getFullEventsByCurrentUser(@PathVariable("user-id") Long userId,
                                                   @PathVariable("event-id") Long eventId) {
        return eventService.getFullEventByIdForCurrentUser(userId, eventId);
    }

    @PatchMapping("/{event-id}")
    public EventFullDto updateByCurrentUser(@PathVariable("user-id") Long userId,
                                            @PathVariable("event-id") Long eventId,
                                            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateByCurrentUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{event-id}/requests")
    public List<ParticipationRequestDto> getRequestsByCurrentUser(@PathVariable("user-id") Long userId,
                                                                  @PathVariable("event-id") Long eventId) {
        return eventService.getRequestsByCurrentUser(userId, eventId);
    }

    @PatchMapping("/{event-id}/requests")
    public EventRequestStatusUpdateResult updateStatus(@PathVariable("user-id") Long userId,
                                                       @PathVariable("event-id") Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        return eventService.updateStatus(userId, eventId, statusUpdateRequest);
    }
}