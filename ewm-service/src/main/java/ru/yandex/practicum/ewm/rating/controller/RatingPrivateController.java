package ru.yandex.practicum.ewm.rating.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.rating.dto.RatingDto;
import ru.yandex.practicum.ewm.rating.dto.RatingRequestDto;
import ru.yandex.practicum.ewm.rating.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/users/{user-id}")
@RequiredArgsConstructor
public class RatingPrivateController {
    public static final String EVENT_ID = "event-id";
    public static final String USER_ID = "user-id";
    public static final String RATING_ID = "rating-id";
    private final RatingService ratingService;

    @PostMapping("/events/{" + EVENT_ID + "}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto create(@PathVariable(USER_ID) Long userId,
                            @PathVariable(EVENT_ID) Long eventId,
                            @Valid @RequestBody RatingRequestDto ratingRequestDto) {
        return ratingService.create(userId, eventId, ratingRequestDto);
    }

    @DeleteMapping("/ratings/{" + RATING_ID + "}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable(USER_ID) Long userId,
                           @PathVariable(RATING_ID) Long ratingId) {
        ratingService.deleteById(userId, ratingId);
    }

    @GetMapping("/ratings")
    public List<RatingDto> getAllRatingsByUser(@PathVariable(USER_ID) Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return ratingService.getAllRatingsByUser(userId, from, size);
    }
}