package ru.yandex.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.event.dto.EventFullDto;
import ru.yandex.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.ewm.event.model.State;
import ru.yandex.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAdminController {
    final EventService eventService;

    @GetMapping
    public List<EventFullDto> getFullEvents(@RequestParam(required = false) List<Long> users,
                                            @RequestParam(required = false) List<State> states,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getFullEvents(users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
    }

    @PatchMapping("/{event-id}")
    public EventFullDto updateByAdmin(@PathVariable("event-id") Long eventId,
                                      @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateByAdmin(eventId, updateEventAdminRequest);
    }
}