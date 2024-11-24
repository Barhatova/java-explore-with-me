package ru.yandex.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.ewm.category.dto.CategoryDto;
import ru.yandex.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.constant.Constants.DATE_FORMAT;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    @NotNull
    String annotation;
    @NotNull
    CategoryDto category;
    Integer confirmedRequests;
    @NotNull
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime eventDate;
    @NotNull
    UserShortDto initiator;
    @NotNull
    Boolean paid;
    @NotNull
    String title;
    Long views;
}
