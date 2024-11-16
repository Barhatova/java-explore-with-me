package ru.yandex.practicum.stats.mapper;

import ru.yandex.practicum.dto.StatDto;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.stats.model.Stat;

@Component
public class StatMapping {

    public static StatDto toStatDto(Stat stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(+1L)
                .build();
    }
}