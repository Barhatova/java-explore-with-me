package ru.yandex.practicum.stats.service;

import ru.yandex.practicum.dto.ParamHitDto;
import ru.yandex.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void createStat(ParamHitDto endpointHitDto);

    List<StatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}