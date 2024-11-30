package ru.yandex.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.event.model.Location;
import ru.yandex.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.constant.Constants.DATE_FORMAT;

@Getter
@Setter
public class EventFullDto {
    private Long id;

    @NotNull
    private String annotation;

    @NotNull
    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime createdOn;

    private String description;

    @NotNull
    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    @JsonFormat(pattern = DATE_FORMAT)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private String state;

    @NotNull
    private String title;

    private Long views;
}
