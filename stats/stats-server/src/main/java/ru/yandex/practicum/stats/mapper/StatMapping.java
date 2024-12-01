package ru.yandex.practicum.stats.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ParamHitDto;
import ru.yandex.practicum.stats.model.Stat;

import java.time.format.DateTimeFormatter;

@Component
public class StatMapping {
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public Stat toParamHit(ParamHitDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        return Stat.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}