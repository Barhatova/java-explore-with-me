package ru.yandex.practicum.ewm.compilation.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.StatsClient;
import ru.yandex.practicum.dto.StatDto;
import ru.yandex.practicum.ewm.compilation.dto.CompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.compilation.model.Compilation;
import ru.yandex.practicum.ewm.compilation.repository.CompilationRepository;
import ru.yandex.practicum.ewm.event.model.Event;
import ru.yandex.practicum.ewm.event.repository.EventRepository;
import ru.yandex.practicum.ewm.exception.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.ewm.constant.Constants.DATE_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationServiceImpl implements CompilationService {
    final EventRepository eventRepository;
    final CompilationRepository compilationRepository;
    final CompilationMapper compilationMapper;
    final StatsClient client;

    @Override
    @Transactional
    public CompilationDto createCompilationAdmin(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.getAllByIdIn(newCompilationDto.getEvents());
            compilation.setEvents(events);
        } else {
            compilation.setEvents(new ArrayList<>());
        }
        if (newCompilationDto.getPinned() == null) {
            compilation.setPinned(false);
        } else {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        compilationRepository.save(compilation);
        return compilationMapper.toDto(compilation, 0L);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilationAdmin(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilationOrThrow(compilationId);
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            List<Event> events = eventRepository.getByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());

        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        compilationRepository.save(compilation);
        Long view = getHitsEvent(compilationId, LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), true);
        return compilationMapper.toDto(compilation, view);
    }

    @Override
    @Transactional
    public void deleteCompilationAdmin(Long compilationId) {
        if (compilationRepository.existsById(compilationId)) {
            compilationRepository.deleteById(compilationId);
            log.info(String.format("Собрание по id не найдено", compilationId));
        }
    }

    @Override
    public List<CompilationDto> findCompilationsPublic(Boolean pinned, Pageable pageable) {
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(c -> compilationMapper.toDto(c, getHitsEvent(c.getId(),
                            LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), true)))
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(c -> compilationMapper.toDto(c, getHitsEvent(c.getId(),
                        LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), true)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findCompilationPublic(Long compilationId) {
        Compilation compilation = getCompilationOrThrow(compilationId);
        return compilationMapper.toDto(compilation, getHitsEvent(compilationId,
                LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), true));
    }

    private Compilation getCompilationOrThrow(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Собрание по id не найдено", compilationId)));
    }

    private Long getHitsEvent(Long eventId, String start, String end, Boolean unique) {
        List<String> uris = new ArrayList<>();
        uris.add("/events/" + eventId);
        List<StatDto> output = client.getStat(start, end, uris, unique);
        Long view = 0L;
        if (!output.isEmpty()) {
            view = output.get(0).getHits();
        }
        return view;
    }
}