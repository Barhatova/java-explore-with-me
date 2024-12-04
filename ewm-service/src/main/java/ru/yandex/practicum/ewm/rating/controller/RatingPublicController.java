package ru.yandex.practicum.ewm.rating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.rating.dto.RatingDto;
import ru.yandex.practicum.ewm.rating.model.RatingSortType;
import ru.yandex.practicum.ewm.rating.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingPublicController {
    public static final String EVENT_ID = "event-id";
    public static final String RATING_ID = "rating-id";
    private final RatingService ratingService;

    @GetMapping("/events/{" + EVENT_ID + "}")
    public List<RatingDto> getAllRatingsByEvent(@PathVariable(EVENT_ID) Long eventId,
                                                @RequestParam(defaultValue = "NEW_AND_USEFUL") RatingSortType sortType,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return ratingService.getAllRatingsByEvent(eventId, sortType, from, size);
    }

    @GetMapping("/{" + RATING_ID + "}")
    public RatingDto getRatingById(@PathVariable(RATING_ID) Long ratingId) {
        return ratingService.getRatingById(ratingId);
    }
}
