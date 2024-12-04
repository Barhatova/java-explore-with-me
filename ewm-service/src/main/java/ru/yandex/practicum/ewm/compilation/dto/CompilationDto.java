package ru.yandex.practicum.ewm.compilation.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    String title;
    List<EventShortDto> events;
    boolean pinned;
}