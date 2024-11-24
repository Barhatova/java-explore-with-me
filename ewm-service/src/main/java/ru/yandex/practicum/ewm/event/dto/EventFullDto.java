package ru.yandex.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.event.model.Location;
import ru.yandex.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.constant.Constants.DATE_FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    @NotNull
    String annotation;
    @NotNull
    CategoryDto category;
    Integer confirmedRequests;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime createdOn;
    String description;
    @NotNull
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime eventDate;
    @NotNull
    UserShortDto initiator;
    @NotNull
    Location location;
    @NotNull
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    String state;
    @NotNull
    String title;
    Long views;
}
