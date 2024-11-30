package ru.yandex.practicum.stats.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.stats.model.EndpointHit;

import java.time.format.DateTimeFormatter;

@Component
public class EndpointHitMapper {
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}