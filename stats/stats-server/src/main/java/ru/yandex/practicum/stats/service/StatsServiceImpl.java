package ru.yandex.practicum.stats.service;

import ru.yandex.practicum.dto.ParamDto;
import ru.yandex.practicum.dto.ParamHitDto;
import ru.yandex.practicum.dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.stats.mapper.DataTimeMapper;
import ru.yandex.practicum.stats.exception.ValidationException;
import ru.yandex.practicum.stats.model.Stat;
import ru.yandex.practicum.stats.repository.StatsRepository;
import ru.yandex.practicum.stats.validator.CreateStatValidator;
import ru.yandex.practicum.stats.validator.GetStatsValidator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                .timestamp(DataTimeMapper.toInstant(newStat.getTimestamp()))
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
    public List<StatDto> getStat(String startTime, String endTime, List<String> uris, boolean unique) {
        GetStatsValidator validator = new GetStatsValidator(new ParamDto(startTime,endTime));
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Невалидные параметры", validator.getMessages());
        }
        List<StatDto> statForOutput;
        List<Stat> stats;
        if (uris == null) {
            stats = statsRepository.getStatByForThePeriod(DataTimeMapper.toInstant(startTime),
                    DataTimeMapper.toInstant(endTime));
        } else {
            stats = statsRepository.getStatByUriForThePeriod(DataTimeMapper.toInstant(startTime),
                    DataTimeMapper.toInstant(endTime),
                    uris);
        }
        statForOutput = this.groupStat(stats, unique);
        return statForOutput;
    }
}
