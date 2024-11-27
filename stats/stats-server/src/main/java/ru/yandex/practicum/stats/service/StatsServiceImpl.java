package ru.yandex.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ParamDto;
import ru.yandex.practicum.dto.ParamHitDto;
import ru.yandex.practicum.dto.StatDto;
import ru.yandex.practicum.stats.exception.ValidationException;
import ru.yandex.practicum.stats.model.Stat;
import ru.yandex.practicum.stats.repository.StatsRepository;
import ru.yandex.practicum.stats.validator.CreateStatValidator;
import ru.yandex.practicum.stats.validator.GetStatsValidator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private final StatsRepository statsRepository;

    @Override
    public void createStat(ParamHitDto newStat) {
        CreateStatValidator validator = new CreateStatValidator(newStat);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Невалидные параметры", validator.getMessages());
        }
        Stat stat = Stat.builder()
                .app(newStat.getApp())
                .uri(newStat.getUri())
                .ip(newStat.getIp())
                .timestamp(newStat.getTimestamp())
                .build();
        statsRepository.save(stat);
    }

    @Override
    public List<StatDto> groupStat(List<Stat> stats, boolean unique) {
        List<StatDto> statForOutput = new ArrayList<>();
        Map<String, List<Stat>> groupStat = stats.stream()
                .collect(Collectors.groupingBy(Stat::getUri));

        for (Map.Entry<String, List<Stat>> stat : groupStat.entrySet()) {
            String key = stat.getKey();
            long count = stat.getValue().size();
            Stat curreentStat = stat.getValue().getFirst();
            if (unique) {
                Set<String> uniqueIp =  stat.getValue().stream()
                        .map(Stat::getIp)
                        .collect(Collectors.toSet());
                count = uniqueIp.size();
            }
            StatDto statDto = StatDto.builder()
                    .app(curreentStat.getApp())
                    .uri(key)
                    .hits(count)
                    .build();
            statForOutput.add(statDto);
        }
        return statForOutput.stream()
                .sorted(Comparator.comparing(StatDto::getHits).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<StatDto> getStat(LocalDateTime  startTime, LocalDateTime endTime, List<String> uris, boolean unique) {
        List<String> urisList = new ArrayList<>();
        GetStatsValidator validator = new GetStatsValidator(new ParamDto(startTime,endTime));
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Невалидные параметры", validator.getMessages());
        }

        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Время начала должно быть раньше окончания");
        }
        List<StatDto> statForOutput;
        List<Stat> stats;
        if (uris == null) {
            stats = statsRepository.getStatByForThePeriod(startTime, endTime);
        } else {
            for (String uri : uris) {
                if (uri.startsWith("[")) {
                    urisList.add(uri.substring(1, uri.length() - 1));
                } else
                    urisList.add(uri);
            }
            stats = statsRepository.getStatByUriForThePeriod(startTime, endTime, urisList);
        }
        statForOutput = groupStat(stats, unique);
        return statForOutput;
    }
}
