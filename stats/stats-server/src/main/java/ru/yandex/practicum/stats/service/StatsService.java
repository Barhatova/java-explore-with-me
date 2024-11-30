package ru.yandex.practicum.stats.service;

import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void create(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}