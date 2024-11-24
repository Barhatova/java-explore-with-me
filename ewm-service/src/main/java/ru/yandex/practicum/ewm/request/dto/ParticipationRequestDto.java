package ru.yandex.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static ru.yandex.practicum.ewm.constant.Constants.DATE_FORMAT;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;
    @JsonFormat(pattern = DATE_FORMAT)
    LocalDateTime created;
    Long event;
    Long requester;
    String status;
}
