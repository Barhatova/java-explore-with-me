package ru.yandex.practicum.ewm.rating.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.rating.dto.RatingDto;
import ru.yandex.practicum.ewm.rating.dto.RatingRequestDto;
import ru.yandex.practicum.ewm.rating.model.Rating;
import ru.yandex.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Component
public class RatingMapper {
    public Rating toRatingFromRatingRequestDto(User user, Event event, RatingRequestDto ratingRequestDto) {
        return Rating.builder()
                .rating(ratingRequestDto.getRating())
                .comment(ratingRequestDto.getComment())
                .event(event)
                .user(user)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public RatingDto toRaringDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .eventId(rating.getEvent().getId())
                .userId(rating.getUser().getId())
                .timestamp(rating.getTimestamp())
                .build();
    }
}