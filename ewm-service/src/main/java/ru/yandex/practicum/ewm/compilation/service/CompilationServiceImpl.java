package ru.yandex.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.compilation.repository.CompilationRepository;
import ru.yandex.practicum.ewm.event.dto.EventShortDto;
import ru.yandex.practicum.ewm.event.mapper.EventMapper;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Запрос на получение списка групп");Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable).getContent();
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        if (compilations.isEmpty()) {
            log.info("Список групп пуст");
            return List.of();
        }
        List<CompilationDto> compilationDtos = compilations.stream()
                .map(compilation -> {
                    List<EventShortDto> eventShortDtos = compilation.getEvents().stream().map(eventMapper::toEventShortDtoFromEvent).toList();
                    return compilationMapper.toCompilationDtoFromCompilation(compilation, eventShortDtos);
                })
                .toList();
        log.info("Список групп получен");
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Запрос на получение группы по идентификатору");
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Группа не найдена", compId)));
        List<EventShortDto> eventShortDtos = compilation.getEvents().stream().map(eventMapper::toEventShortDtoFromEvent).toList();
        CompilationDto compilationDto = compilationMapper.toCompilationDtoFromCompilation(compilation, eventShortDtos);
        log.info("Группа по идентификатору получена");
        return compilationDto;
    }


    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        log.info("Запрос на создание группы");
        Set<Event> events = new HashSet<>();
        if (newCompilationDto.getEvents() != null) {
            events.addAll(eventRepository.findAllById(newCompilationDto.getEvents()));
        }
        Compilation compilation = compilationMapper.toCompilationFromNewCompilationDto(newCompilationDto, events);
        compilation = compilationRepository.save(compilation);
        List<EventShortDto> eventShortDtos = compilation.getEvents().stream().map(eventMapper::toEventShortDtoFromEvent).toList();
        CompilationDto compilationDto = compilationMapper.toCompilationDtoFromCompilation(compilation, eventShortDtos);
        log.info("Группа создана");
        return compilationDto;
    }

    @Override
    @Transactional
    public void deleteById(Long compId) {
        log.info("Запрос на удаление группы по идентификатору");
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(
                    String.format("Группа не найдена", compId));
        }
        compilationRepository.deleteById(compId);
        log.info("Группа по идентификатору удалена");
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Запрос на изменение группы");
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Группа не найдена", compId)));
        StringBuilder updatedFieldsLog = new StringBuilder();
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
            updatedFieldsLog.append("Title|");
        }
        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            compilation.setEvents(events);
            updatedFieldsLog.append("Events|");
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
            updatedFieldsLog.append("Pinned|");
        }
        String updatedFields = updatedFieldsLog.toString().replaceAll("\\|$", "").replace("|", ", ");
        compilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = compilationMapper.toCompilationDtoFromCompilation(compilation, compilation.getEvents().stream()
                .map(eventMapper::toEventShortDtoFromEvent)
                .toList());
        log.info("Группа изменена");
        return compilationDto;
    }
}