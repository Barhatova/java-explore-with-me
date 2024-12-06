package ru.yandex.practicum.ewm.rating.service;

import ru.yandex.practicum.ewm.rating.dto.RatingDto;
import ru.yandex.practicum.ewm.rating.dto.RatingRequestDto;
import ru.yandex.practicum.ewm.rating.model.RatingSortType;

import java.util.List;

public interface RatingService {
    RatingDto create(Long userId, Long eventId, RatingRequestDto ratingRequestDto);

    void deleteById(Long userId, Long ratingId);

    RatingDto getRatingById(Long ratingId);

    List<RatingDto> getAllRatingsByUser(Long userId, int from, int size);

    List<RatingDto> getAllRatingsByEvent(Long eventId, RatingSortType sortType, int from, int size);
}