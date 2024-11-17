package ru.yandex.practicum.stats.service;

import ru.yandex.practicum.dto.ParamHitDto;
import ru.yandex.practicum.dto.StatDto;
import ru.yandex.practicum.stats.model.Stat;
import java.util.List;

public interface StatsService {

    void createStat(ParamHitDto stat);

    List<StatDto> groupStat(List<Stat> stats, boolean unique);

    List<StatDto> getStat(String startTime, String endTime, List<String> uris, boolean unique);
}