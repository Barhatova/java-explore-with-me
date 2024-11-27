package ru.yandex.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{user-id}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable("user-id") Long userId) {
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable("user-id") Long userId, @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{request-id}/cancel")
    public ParticipationRequestDto cancel(@PathVariable("user-id") Long userId, @PathVariable("request-id") Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}