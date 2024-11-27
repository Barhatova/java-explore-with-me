package ru.yandex.practicum.ewm.compilation.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.ewm.event.dto.EventShortDto;
import ru.yandex.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Set;

@Component
public class CompilationMapper {
    public Compilation toCompilationFromNewCompilationDto(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .events(events)
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public CompilationDto toCompilationDtoFromCompilation(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events)
                .build();
    }
}