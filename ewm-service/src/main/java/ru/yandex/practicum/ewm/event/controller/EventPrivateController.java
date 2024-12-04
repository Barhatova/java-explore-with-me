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
        public static final String USER_ID = "user-id";
        public static final String EVENT_ID = "event-id";
        private final EventService eventService;

        @GetMapping
        public List<EventShortDto> getEventsByCurrentUser(@PathVariable(USER_ID) Long userId,
                                                          @RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
            return eventService.getEventsByCurrentUser(userId, from, size);
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public EventFullDto create(@PathVariable(USER_ID) Long userId,
                                   @Valid @RequestBody NewEventDto newEventDto) {
            return eventService.create(userId, newEventDto);
        }

        @GetMapping("/{" + EVENT_ID + "}")
        public EventFullDto getFullEventsByCurrentUser(@PathVariable(USER_ID) Long userId,
                                                       @PathVariable(EVENT_ID) Long eventId) {
            return eventService.getFullEventByIdForCurrentUser(userId, eventId);
        }

        @PatchMapping("/{" + EVENT_ID + "}")
        public EventFullDto updateByCurrentUser(@PathVariable(USER_ID) Long userId,
                                                @PathVariable(EVENT_ID) Long eventId,
                                                @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
            return eventService.updateByCurrentUser(userId, eventId, updateEventUserRequest);
        }

        @GetMapping("/{" + EVENT_ID + "}/requests")
        public List<ParticipationRequestDto> getRequestsByCurrentUser(@PathVariable(USER_ID) Long userId,
                                                                      @PathVariable(EVENT_ID) Long eventId) {
            return eventService.getRequestsByCurrentUser(userId, eventId);
        }

        @PatchMapping("/{" + EVENT_ID + "}/requests")
        public EventRequestStatusUpdateResult updateStatus(@PathVariable(USER_ID) Long userId,
                                                           @PathVariable(EVENT_ID) Long eventId,
                                                           @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
            return eventService.updateStatus(userId, eventId, statusUpdateRequest);
        }
    }